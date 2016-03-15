package cc.haoduoyu.umaru.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;

import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.ui.activities.NowPlayingActivity;


/**
 * Service播放器，广播接受命令并控制MediaPlayer
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate()");
        instance = this;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (player == null) {
            player = new Player(this);
        }

//        if (PlayerController.getNowPlaying() != null) {//导致音乐被清空
//            LogUtils.d("startForeground");
//        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新Notification
     */
    public void notifyNowPlayingNotification() {
        notificationManager.notify(NOTIFICATION_ID, getNotification());
        LogUtils.d("notifyNowPlayingNotification");
    }

    /**
     * 创建Notification
     */
    private Notification getNotification() {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        Intent intent1 = new Intent(this, PlayReceiver.class);

        Intent intent2 = new Intent(this, NowPlayingActivity.class);
        intent2.putExtra(NowPlayingActivity.EXTRA_NOW_PLAYING, PlayerController.getNowPlaying());

        NotificationCompat.MediaStyle mediaStyle = new NotificationCompat.MediaStyle();
        mediaStyle
                .setShowActionsInCompactView(0, 1, 2)
                .setCancelButtonIntent(PendingIntent.getBroadcast(this, 1, intent1.setAction(ACTION_STOP), 0))
                .setShowCancelButton(true);

        notification
                .setStyle(mediaStyle)
                .setColor(this.getResources().getColor(R.color.md_grey_800))
                .setShowWhen(false)//是否允许显示时间
                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待
                .setOnlyAlertOnce(true)//只提醒一次
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//                .setContentIntent(PendingIntent.getActivity(getInstance(), 0, intent2,
//                        PendingIntent.FLAG_UPDATE_CURRENT));

        // 这种专辑图标
        if (getArt() == null) {
            notification.setLargeIcon(
                    BitmapFactory.decodeResource(this.getResources(), R.mipmap.default_artwork));
        } else {
            notification.setLargeIcon(getArt());
        }

        // 添加控制按钮
        //添加Previous按钮
        notification.addAction(R.mipmap.ic_skip_previous_white_48dp, "action_previous",
                PendingIntent.getBroadcast(this, 1, intent1.setAction(ACTION_PREVIOUS), PendingIntent.FLAG_UPDATE_CURRENT));
        // 添加Play/Pause切换按钮
        if (player.isPlaying()) {
            notification.addAction(R.mipmap.ic_pause_white_48dp, "action_pause",
                    PendingIntent.getBroadcast(this, 1, intent1.setAction(ACTION_TOGGLE_PLAY), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.mipmap.ic_play_arrow_white_24dp);
        } else {
            notification
                    .addAction(R.mipmap.ic_play_arrow_white_48dp, "action_play",
                            PendingIntent.getBroadcast(this, 1, intent1.setAction(ACTION_TOGGLE_PLAY), PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.mipmap.ic_pause_white_24dp);
        }
        // 添加Next按钮
        notification.addAction(R.mipmap.ic_skip_next_white_48dp, "action_next",
                PendingIntent.getBroadcast(this, 1, intent1.setAction(ACTION_NEXT), PendingIntent.FLAG_UPDATE_CURRENT));

        // 更新正在播放的信息
        if (getNowPlaying() != null) {
            notification.setContentTitle(getNowPlaying().getSongTitle())
                    .setContentText(getNowPlaying().getArtistName())
                    .setSubText(getNowPlaying().getAlbumName());
        } else {
            notification.setContentTitle("").setContentText("").setSubText("");
        }
        return notification.build();
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
                    //前台Service
                    instance.startForeground(NOTIFICATION_ID, instance.getNotification());
                    //不写这句的话，最近任务列表清除Service会onCreate()，音乐停止；
                    //写的话，最近任务列表清除Service不会onCreate()音乐正常；
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
        LogUtils.i("onDestroy");
        super.onDestroy();
    }

    /**
     * 停止
     */
    public void stop() {
        LogUtils.i("stop()");
        notificationManager.cancel(NOTIFICATION_ID);
        player.finish();
        player = null;
        stopForeground(true);
        instance = null;
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

    public Bitmap getArt() {
        return player.getArt();
    }

}
