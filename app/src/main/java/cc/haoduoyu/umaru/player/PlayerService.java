package cc.haoduoyu.umaru.player;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;

import cc.haoduoyu.umaru.Umaru;
import cc.haoduoyu.umaru.model.Song;


/**
 * Service播放器
 * Created by XP on 16/1/23.
 */
public class PlayerService extends Service {

    private static final int NOTIFICATION_ID = 1;

    public static final String ACTION_BEGIN = "cc.haoduoyu.umaru.player.ACTION_BEGIN";
    public static final String ACTION_TOGGLE_PLAY = "cc.haoduoyu.umaru.player.ACTION_TOGGLE_PLAY";
    public static final String ACTION_PLAY = "cc.haoduoyu.umaru.player.ACTION_PLAY";
    public static final String ACTION_NEXT = "cc.haoduoyu.umaru.player.ACTION_NEXT";
    public static final String ACTION_PREVIOUS = "cc.haoduoyu.umaru.player.ACTION_PREVIOUS";
    public static final String ACTION_PAUSE = "cc.haoduoyu.umaru.player.ACTION_PAUSE";
    public static final String ACTION_STOP = "cc.haoduoyu.umaru.player.ACTION_STOP";
    public static final String ACTION_SEEK = "cc.haoduoyu.umaru.player.ACTION_SEEK";
    public static final String ACTION_SET_QUEUE = "cc.haoduoyu.umaru.player.SET_QUEUE";
    public static final String ACTION_DELETE_SONG = "cc.haoduoyu.umaru.player.DELETE_SONG";
    public static final String ACTION_SET_PRES = "cc.haoduoyu.umaru.player.SET_PRES";

    public static final String EXTRA_QUEUE = "extra_queue";
    public static final String EXTRA_POSITION = "extra_position";
    public static final String EXTRA_SEEK_POSITION = "extra_seek_position";
    public static final String EXTRA_STATE = "extra_state";

    /**
     * 全局变量
     */
    private static PlayerService instance;
    private Player player;
    private NotificationManager notificationManager;
    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("service onCreate() call");
        context = Umaru.getContext();
        if (instance == null) {
            instance = this;
        } else {
            LogUtils.i("Attempt again create service");
            stopSelf();
            return;
        }

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (player == null) {
            player = new Player(this);
        }

    }

    /**
     * 广播接收器，接收对Player的控制,静态注册
     */
    public static class PlayReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                LogUtils.i("not get action");
                return;
            }

            switch (intent.getAction()) {
                case ACTION_BEGIN:
                    instance.player.begin();
                    break;
                case ACTION_SET_QUEUE:
                    int position = intent.getIntExtra(EXTRA_POSITION, 0);
                    ArrayList<Song> songList = intent.getParcelableArrayListExtra(EXTRA_QUEUE);
                    instance.player.setQueue(songList, position);
                    break;
                case ACTION_TOGGLE_PLAY:
                    instance.player.togglePlay();
                    break;
                case ACTION_PLAY:
                    instance.player.play();
                    break;
                case ACTION_NEXT:
                    instance.player.next();
                    break;
                case ACTION_PREVIOUS:
                    instance.player.previous();
                    break;
                case ACTION_PAUSE:
                    instance.player.pause();
                    break;
                case ACTION_STOP:
                    instance.stop();
                    break;
                case ACTION_SEEK:
                    instance.player.setSeek(intent.getIntExtra(EXTRA_SEEK_POSITION, 0));
                    break;
                case ACTION_DELETE_SONG:
                    break;
                case ACTION_SET_PRES:
                    instance.getPlayer().setPreferences(intent.getIntExtra(EXTRA_STATE, Player.REPEAT_ALL));
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        LogUtils.i("onDestroy is called");
        super.onDestroy();
    }

    /**
     * 结束
     */
    public void stop() {
        LogUtils.i("stop() called");
        finish();
    }

    /**
     * 结束并清空资源
     */
    public void finish() {
        LogUtils.i("finish() called");

        notificationManager.cancel(NOTIFICATION_ID);
        player.finish();
        player = null;
        stopForeground(true);
        instance = null;
        context = null;
        stopSelf();
    }

    /**
     * 获得Service实例
     */
    public static PlayerService getInstance() {
        return instance;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * 获得当前正在播放的音乐
     */
    public Song getNowPlaying() {
        return player.getNowPlaying();
    }

//  public Bitmap getArt() {
//    return player.getArt();
//  }
}
