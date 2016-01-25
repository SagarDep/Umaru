package cc.haoduoyu.umaru.activities;

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

import com.apkfuns.logutils.LogUtils;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.player.Player;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.utils.PreferencesUtils;

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

    @OnClick(R.id.previous)
    void previous() {
        PlayerController.previous();
//        seekBar.setMax(Integer.MAX_VALUE);
        seekBar.setProgress(0);
    }

    @OnClick(R.id.next)
    void next() {
        PlayerController.next();
        observer.stop();
//        seekBar.setMax(Integer.MAX_VALUE);
//        seekBar.setProgress(Integer.MAX_VALUE);
    }

    @OnClick(R.id.playpausefloating)
    void playPause() {
        PlayerController.togglePlay();
    }

    /**
     * 广播接收器，更新歌曲信息 ,动态注册 ，Player.updateNowPlaying发送，另见PlayController
     */
    public class UpdateNowPlayingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("received");
            Player.PlayerInfo info = intent.getExtras().getParcelable(Player.EXTRA_NAME);
            LogUtils.d(info);
            if (info != null) {
                Song song = PlayerController.getNowPlaying();
                LogUtils.d(song);
                if (song != null) {
                    //当begin()的时候，此时的isPlaying为false
                    //歌曲处于正在播放时，并且seekbar没有启动，那么就启动它
                    //因为这里时第一次启动seekbar
                    if (!info.isPlaying && !observer.isRunning()) {
                        new Thread(observer).start();
                    }
                    songTitle.setText(song.getSongTitle());
                    songArtist.setText(song.getArtistName() + " | " + song.getAlbumName());
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
    }

}
