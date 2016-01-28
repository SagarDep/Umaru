package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.api.ArtistInfo;
import cc.haoduoyu.umaru.api.MusicFactory;
import cc.haoduoyu.umaru.api.MusicService;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.player.Player;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.volleyUtils.GsonRequest;
import cc.haoduoyu.umaru.widgets.PlayPauseDrawable;
import retrofit2.Callback;

/**
 * Created by XP on 2016/1/9.
 */
public class NowPlayingActivity extends BaseActivity {

    @Bind(R.id.album_art)
    ImageView albumArt;
    @Bind(R.id.shuffle)
    ImageView shuffle;
    @Bind(R.id.repeat)
    ImageView repeat;
    @Bind(R.id.previous)
    MaterialIconView previous;
    @Bind(R.id.next)
    MaterialIconView next;
    @Bind(R.id.playpausefloating)
    FloatingActionButton fab;
    @Bind(R.id.song_progress)
    SeekBar seekBar;
    @Bind(R.id.song_title)
    TextView songTitle;
    @Bind(R.id.song_artist)
    TextView songArtist;

    PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable();
    public static final String EXTRA_NOW_PLAYING = "extra_now_playing";
    private Song song;
    private SeekObserver observer = null;
    private int iconIndex;

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

        Intent intent = getIntent();
        updateNowPlayingReceiver = new UpdateNowPlayingReceiver();
        song = intent.getExtras().getParcelable(EXTRA_NOW_PLAYING);
        LogUtils.d(song);


        initViews();
        observer = new SeekObserver();

        iconIndex = PreferencesUtils.getInteger(this, Player.PREFERENCES_STATE, Player.REPEAT_NONE);
        loadArtistImgWithVolley(song);
        initFab();

    }

    private void initFab() {
        fab.setImageDrawable(playPauseDrawable);
        if (PlayerController.isPlaying()||!PlayerController.isPlaying()) {
            playPauseDrawable.transformToPause(false);
        } else {
            playPauseDrawable.transformToPlay(false);
        }
        LogUtils.d(PlayerController.isPlaying());

    }

    /**
     * 初始化View
     */
    public void initViews() {


        seekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());

        if (song != null) {
            songTitle.setText(song.getSongTitle());
            songArtist.setText(song.getArtistName() + "|" + song.getAlbumName());
            Bitmap reflectedImage;
//            if (FetchUtils.fetchAlbumArtLocal(song.getmAlbumId()) == null) {


//            } else {
//                albumArt.setImageURI(FetchUtils.fetchArtByAlbumId(song.getmAlbumId()));
//            }

            seekBar.setMax((int) PlayerController.getDuration());
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

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b && !touchingProgressBar) {
                onStartTrackingTouch(seekBar);
                onStopTrackingTouch(seekBar);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            observer.stop();
            touchingProgressBar = true;
        }

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
            stop = false;
            while (!stop) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekBar.setProgress((int) PlayerController.getCurrentPosition());
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
            LogUtils.v("runnable stop ");
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

    @OnClick(R.id.previous)
    void previous() {
        PlayerController.previous();
        seekBar.setProgress(0);
    }

    @OnClick(R.id.next)
    void next() {
        PlayerController.next();
        observer.stop();
    }

    @OnClick(R.id.playpausefloating)
    void playPause() {
        PlayerController.togglePlay();
        updatePlayPauseFloatingButton();
    }

    /**
     * 广播接收器，更新歌曲信息 ,动态注册 ，Player.updateNowPlaying发送，另见PlayController
     */
    public class UpdateNowPlayingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Player.PlayerInfo info = intent.getExtras().getParcelable(Player.EXTRA_INFO);
            if (info != null) {
                Song song = PlayerController.getNowPlaying();
                LogUtils.d("received:" + song);
                if (song != null) {
                    //当begin()的时候，此时的isPlaying为false
                    //歌曲处于正在播放时，并且seekbar没有启动，那么就启动它
                    //因为这里时第一次启动seekbar
                    if (!info.isPlaying && !observer.isRunning()) {
                        new Thread(observer).start();
                    }
                    songTitle.setText(song.getSongTitle());
                    songArtist.setText(song.getArtistName() + " | " + song.getAlbumName());
                    loadArtistImgWithVolley(song);//加载图片
                }
            }

            if (info.isPlaying) {
                //正在播放时将togglebutton设置为暂停图片
//                        mTogglePlay.setImageResource(R.mipmap.ic_pause_white_48dp);
            } else {
                //在begin()的时候此时歌曲没有播放，为正在准备中，
                //在此时将seekbar的最大值设置为歌曲的总长度, 进度设置为当前进度，由于准备中，当前进度为0
                seekBar.setMax((int) PlayerController.getDuration());
                seekBar.setProgress((int) PlayerController.getCurrentPosition());
//                        mTogglePlay.setImageResource(R.mipmap.ic_play_arrow_white_48dp);
            }
        }
    }


    private void loadArtistImgWithRetrofit(Song song) throws UnsupportedEncodingException {

        String artistName = URLEncoder.encode(song.getArtistName(), "utf-8");
        MusicFactory.getMusicService().getArtistInfo(artistName).enqueue(new Callback<ArtistInfo>() {
            @Override
            public void onResponse(retrofit2.Response<ArtistInfo> response) {
                ArtistInfo info = response.body();
                LogUtils.d(info.getArtist());
                LogUtils.d(info.getArtist().getImage().get(5).getSize());
                LogUtils.d(info.getArtist().getImage().get(5).getUrl());//得不到数据？？？
                Glide.with(NowPlayingActivity.this)
                        .load(info.getArtist().getImage().get(5).getUrl()).crossFade().into(albumArt);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void loadArtistImgWithVolley(Song song) {
        String artistName = null;
        try {
            artistName = URLEncoder.encode(song.getArtistName(), "utf-8");

            String formatString = String.format("%s?method=%s&lang=%s&artist=%s&format=json&api_key=%s", MusicService.LAST_FM_URL,
                    "artist.getInfo", "zh", artistName, MusicService.API_KEY);
            executeRequest(new GsonRequest<>(formatString, ArtistInfo.class, new Response.Listener<ArtistInfo>() {
                @Override
                public void onResponse(ArtistInfo response) {
                    LogUtils.d(response.getArtist().getImage().get(3).getUrl());
                    LogUtils.d(response.getArtist().getImage().get(3).getSize());
                    Glide.with(NowPlayingActivity.this)
                            .load(response.getArtist().getImage().get(3).getUrl()).crossFade().into(albumArt);
                }
            }));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
