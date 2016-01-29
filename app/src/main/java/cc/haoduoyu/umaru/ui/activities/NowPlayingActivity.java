package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.model.ArtistInfo;
import cc.haoduoyu.umaru.api.MusicFactory;
import cc.haoduoyu.umaru.api.MusicService;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.player.Player;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.utils.ShareUtils;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.utils.volleyUtils.GsonRequest;
import cc.haoduoyu.umaru.widgets.PlayPauseDrawable;
import retrofit2.Callback;

/**
 * Created by XP on 2016/1/9.
 */
public class NowPlayingActivity extends BaseActivity {

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.shuffle)
    ImageView shuffle;
    @Bind(R.id.menu)
    ImageView menu;
    @Bind(R.id.playpausefloating)
    FloatingActionButton fab;
    @Bind(R.id.song_progress)
    SeekBar seekBar;
    @Bind(R.id.song_title)
    TextView songTitle;
    @Bind(R.id.song_artist_album)
    TextView songArtistAlbum;
    @Bind(R.id.song_duration)
    TextView songDuration;
    @Bind(R.id.song_elapsed_time)
    TextView songElapsedTime;

    PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    public static final String EXTRA_NOW_PLAYING = "extra_now_playing";
    private Song song;
    private SeekObserver observer = null;
    //    private int iconIndex;
    private String summary;

    private UpdateNowPlayingReceiver updateNowPlayingReceiver;


    public static void startIt(Song song, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, NowPlayingActivity.class);
        intent.putExtra(EXTRA_NOW_PLAYING, song);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nowplaying);
        ButterKnife.bind(this);

        song = getIntent().getExtras().getParcelable(EXTRA_NOW_PLAYING);//来自adapter

        LogUtils.d(song);

        initViews();

        updateNowPlayingReceiver = new UpdateNowPlayingReceiver();

//        iconIndex = PreferencesUtils.getInteger(this, Player.PREFERENCES_STATE, Player.REPEAT_NONE);
        loadArtistInfoWithVolley(song);


    }

    //初始化播放暂停按钮
    private void initFab() {
        fab.setImageDrawable(playPauseDrawable);
        if (PlayerController.isPlaying() || !PlayerController.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
        LogUtils.d("isPlaying:" + PlayerController.isPlaying());

    }


    /**
     * 初始化View
     */
    public void initViews() {
        initFab();
        updateShuffle();
        MaterialDrawableBuilder mBuilder = MaterialDrawableBuilder.with(this)
                .setIcon(MaterialDrawableBuilder.IconValue.MENU)
                .setSizeDp(30);
        menu.setImageDrawable(mBuilder.build());

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingUpPanelLayout.setPanelHeight(0);


        observer = new SeekObserver();
        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        if (song != null) {//第一次初始化Song
            songTitle.setText(song.getSongTitle());
            songArtistAlbum.setText(song.getArtistName() + " | " + song.getAlbumName());
            songDuration.setText(Utils.durationToString(song.getDuration()));
            songElapsedTime.setText("00:01");
            seekBar.setMax((int) song.getDuration());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        observer.stop();
        if (updateNowPlayingReceiver != null) {
            unregisterReceiver(updateNowPlayingReceiver);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(observer).start();
        registerReceiver(updateNowPlayingReceiver, new IntentFilter(Player.UPDATE_SONG_INFO));
    }

    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        boolean touchingProgressBar = false;

        //当进度发生改变时执行
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b && !touchingProgressBar) {
                onStartTrackingTouch(seekBar);
                onStopTrackingTouch(seekBar);
            }
        }

        //在进度开始改变时执行
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            observer.stop();
            touchingProgressBar = true;
        }

        //停止拖动时执行
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            PlayerController.seek(seekBar.getProgress());
            new Thread(observer).start();
            touchingProgressBar = false;
        }
    }

    class SeekObserver implements Runnable {
        private boolean stop = false;

        @Override
        public void run() {
            LogUtils.d("SeekObserver run...");
            stop = false;
            while (!stop) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress((int) PlayerController.getCurrentPosition());
                        songElapsedTime.setText(Utils.durationToString(PlayerController.getCurrentPosition()));
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            LogUtils.v("SeekObserver stop...");
            stop = true;
        }

        public boolean isRunning() {
            return !stop;
        }
    }

    public void updatePlayPauseFloatingButton() {
        if (PlayerController.isPlaying()) {
            playPauseDrawable.transformToPause(true);
            LogUtils.d(PlayerController.isPlaying());
        } else {
            playPauseDrawable.transformToPlay(true);
            LogUtils.d(PlayerController.isPlaying());

        }
    }

    //更新随机图标状态
    private void updateShuffle() {
        MaterialDrawableBuilder sBuilder = MaterialDrawableBuilder.with(this)
                .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                .setSizeDp(30).setColor(R.color.colorAccent);
        TypedValue typeValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.iconColor, typeValue, true);
        int color = typeValue.data;
        getTheme().resolveAttribute(R.attr.accentColor, typeValue, true);
        int color2 = typeValue.data;
        if (Player.SHUFFLE == PlayerController.getPlayState()) {
            sBuilder.setColor(color2);
        } else {
            sBuilder.setColor(color);
        }
        shuffle.setImageDrawable(sBuilder.build());
        LogUtils.d(PlayerController.getPlayState());
    }

    private void closePanel() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @OnClick(R.id.previous)
    void previous() {
        PlayerController.previous();
        seekBar.setProgress(0);
    }

    @OnClick(R.id.next)
    void next() {
        PlayerController.next();
//        observer.stop();
    }

    @OnClick(R.id.playpausefloating)
    void playPause() {
        PlayerController.togglePlay();
        updatePlayPauseFloatingButton();
    }

    @OnClick(R.id.shuffle)
    void shuffle() {
        PlayerController.cyclePlayState();
        updateShuffle();
    }

    @OnClick(R.id.menu)
    void menu() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }


    @OnClick(R.id.panel_close)
    void closePanelLayout() {
        closePanel();
    }

    @OnClick(R.id.panel_song)
    void panelSong() {
        closePanel();
        new MaterialDialog.Builder(this)
                .title(R.string.panel_song)
                .content(Utils.getSongContent(song))
                .positiveText(R.string.agree)
                .show();
    }

    @OnClick(R.id.panel_artist)
    void panelArtist() {
        closePanel();
        SpannableStringBuilder content = null;
        if (!TextUtils.isEmpty(summary)) {
            content = new SpannableStringBuilder("  " + summary.split(getString(R.string.href))[0]);
            String link = summary.substring(content.length() - 2, summary.length());
            content.append("\n").append(Html.fromHtml(link));
        }
        LogUtils.d("content: " + content);
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.artist))
                .content(!TextUtils.isEmpty(summary) ? content : getString(R.string.net_error))
                .positiveText(R.string.agree)
                .show();
    }

    @OnClick(R.id.panel_share)
    void panelShare() {
        closePanel();
        ShareUtils.share(this, song.getDisplayName());
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            super.onBackPressed();

        }
    }

    /**
     * 广播接收器，更新歌曲信息 ,动态注册 ，Player.updateNowPlaying发送，另见PlayController
     */
    public class UpdateNowPlayingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Player.PlayerInfo info = PlayerController.getInfo();
            if (info != null) {
                Song song = PlayerController.getNowPlaying();
                LogUtils.d("action: " + intent.getExtras().getString(Player.EXTRA_ACTION)
                        + "received: " + song.getDisplayName());
                if (song != null) {
//                    if (!info.isPlaying && !observer.isRunning()) {
//                        new Thread(observer).start();
//                    }
                    songTitle.setText(song.getSongTitle());
                    songArtistAlbum.setText(song.getArtistName() + " | " + song.getAlbumName());
                    loadArtistInfoWithVolley(song);//加载图片
                }
            }
            seekBar.setMax((int) PlayerController.getDuration());
            seekBar.setProgress((int) PlayerController.getCurrentPosition());
        }
    }


    private void loadArtistInfoWithRetrofit(Song song) {
        try {
            String artistName = null;
            if (!TextUtils.isEmpty(song.getArtistName()))
                artistName = URLEncoder.encode(song.getArtistName(), "utf-8");
            MusicFactory.getMusicService().getArtistInfo(artistName).enqueue(new Callback<ArtistInfo>() {
                @Override
                public void onResponse(retrofit2.Response<ArtistInfo> response) {
                    ArtistInfo info = response.body();
                    LogUtils.d(info.getArtist());
                    LogUtils.d(info.getArtist().getImage().get(3).getSize());
                    LogUtils.d(info.getArtist().getImage().get(3).getUrl());//得不到数据？？？
                    Glide.with(NowPlayingActivity.this)
                            .load(info.getArtist().getImage().get(3).getUrl()).crossFade().into(albumArt);
                }

                @Override
                public void onFailure(Throwable t) {
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void loadArtistInfoWithVolley(Song song) {
        String artistName = null;
        try {
            if (!TextUtils.isEmpty(song.getArtistName()))
                artistName = URLEncoder.encode(song.getArtistName(), "utf-8");
            String formatString = String.format(getString(R.string.artist_format_string), MusicService.LAST_FM_URL,
                    "artist.getInfo", "zh", artistName, MusicService.API_KEY);
            executeRequest(new GsonRequest<>(formatString, ArtistInfo.class, new Response.Listener<ArtistInfo>() {
                @Override
                public void onResponse(ArtistInfo response) {
                    summary = response.getArtist().getBio().getSummary();
                    LogUtils.d(response.getArtist().getImage().get(3).getUrl());
                    LogUtils.d(response.getArtist().getImage().get(3).getSize());
                    Glide.with(NowPlayingActivity.this)
                            .load(response.getArtist().getImage().get(3).getUrl()).crossFade().into(albumArt);
                }
            }).setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
