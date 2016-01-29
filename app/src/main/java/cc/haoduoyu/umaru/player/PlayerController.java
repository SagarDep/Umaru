package cc.haoduoyu.umaru.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.utils.PreferencesUtils;


/**
 * 播放控制器，发送广播控制Service中的Player
 * Created by XP on 16/1/23.
 */
public class PlayerController {

    private static Context mContext;
    private static Player.PlayerInfo info;
    private static Bitmap art;

    /**
     * 启动播放器服务
     */
    public static void startService(Context context) {
//    if (mContext == null) {
        mContext = context;
        Intent serviceIntent = new Intent(context, PlayerService.class);
        context.startService(serviceIntent);
//    }
    }

    /**
     * 得到给Service发送广播的Intent,PlayerService.PlayReceiver用于接收广播
     */
    public static Intent getBroadCastIntent(String action) {
        Intent intent = new Intent(mContext, PlayerService.PlayReceiver.class);
        if (action != null) intent.setAction(action);
        return intent;
    }

    /**
     * 发送切换广播
     */
    public static void togglePlay() {
        if (info != null) {
            info.currentPosition = getCurrentPosition();
            info.currentTime = System.currentTimeMillis();
            info.isPlaying = !info.isPlaying;
        }
        mContext.sendBroadcast(getBroadCastIntent(PlayerService.ACTION_TOGGLE_PLAY));
    }

    /**
     * 发送设置队列和位置广播,PlayService接受
     */
    public static void setQueueAndPosition(final List<Song> songs, final int position) {
        if (info != null) {
            info.queue = songs;
            info.queuePosition = position;
        }
        LogUtils.d(songs.get(position).getSongTitle() + PlayerService.EXTRA_POSITION);
        Intent intent = getBroadCastIntent(PlayerService.ACTION_SET_QUEUE);
        intent.putExtra(PlayerService.EXTRA_POSITION, position);//位置
        intent.putParcelableArrayListExtra(PlayerService.EXTRA_QUEUE, (ArrayList) songs);//歌曲列表
        mContext.sendBroadcast(intent);//发送SET_QUEUE广播
    }

    /**
     * 发送开始广播
     */
    public static void begin() {
        if (info != null) {
            info.queuePosition = 0;
            info.currentTime = System.currentTimeMillis();
        }
        mContext.sendBroadcast(getBroadCastIntent(PlayerService.ACTION_BEGIN));
    }

    /**
     * 发送下一首广播
     */
    public static void next() {
        if (info != null && info.queuePosition < info.queue.size())
            info.queuePosition++;
        mContext.sendBroadcast(getBroadCastIntent(PlayerService.ACTION_NEXT));
    }

    /**
     * 发送上一首广播
     */
    public static void previous() {
        if (info != null) {
            info.currentPosition = 0;
            info.currentTime = System.currentTimeMillis();

            if (info.queuePosition > 0) {
                info.queuePosition--;
            }
        }
        mContext.sendBroadcast(getBroadCastIntent(PlayerService.ACTION_PREVIOUS));
    }

    /**
     * 发送暂停广播
     */
    public static void pause() {
        mContext.sendBroadcast(getBroadCastIntent(PlayerService.ACTION_PAUSE));
    }

    /**
     * 发送停止广播
     */
    public static void stop() {
        mContext.sendBroadcast(getBroadCastIntent(PlayerService.ACTION_STOP));
        mContext = null;
        info = null;
        art = null;
    }

    /**
     * 发送SEEK广播
     */
    public static void seek(final int progress) {
        if (info != null) {
            info.currentPosition = progress;
            info.currentTime = System.currentTimeMillis();
        }
        Intent intent = getBroadCastIntent(PlayerService.ACTION_SEEK);
        intent.putExtra(PlayerService.EXTRA_SEEK_POSITION, progress);
        mContext.sendBroadcast(intent);
    }

    public static void setPlayState(int state) {
        PreferencesUtils.setInteger(mContext, Player.PREFERENCES_STATE, state);//Player中get
        Intent intent = getBroadCastIntent(PlayerService.ACTION_SET_PRES);
        intent.putExtra(PlayerService.EXTRA_STATE, state);
        mContext.sendBroadcast(intent);
    }

    public static void cyclePlayState() {
        if (Player.SHUFFLE == PreferencesUtils.getInteger(mContext, Player.PREFERENCES_STATE, Player.REPEAT_NONE)) {
            setPlayState(Player.REPEAT_NONE);
        } else {
            setPlayState(Player.SHUFFLE);

        }
    }

    public static int getPlayState() {
        return PreferencesUtils.getInteger(mContext, Player.PREFERENCES_STATE, Player.REPEAT_NONE);
    }

    public static Player.PlayerInfo getInfo() {
        return info;
    }

    public static long getCurrentPosition() {
        if (info == null) return 0;
        if (!isPlaying()) return info.currentPosition;

        long dT = System.currentTimeMillis() - info.currentTime;
        return info.currentPosition + dT;
    }

    public static long getDuration() {
        if (info != null) {
            return info.duration;
        }
        return Integer.MAX_VALUE;
    }

    public static boolean isPlaying() {
        return info != null && info.isPlaying;
    }

    public static Song getNowPlaying() {
        if (info != null && info.queuePosition < info.queue.size()) {
            return info.queue.get(info.queuePosition);
        }
        return null;
    }

    //不写static导致
    //java.lang.RuntimeException: Unable to instantiate receiver
    // cc.haoduoyu.umaru.player.PlayerController$UpdateSongReceiver:
    // java.lang.InstantiationException:can't instantiate class cc.haoduoyu.umaru.player.PlayerController$UpdateSongReceiver;
    // no empty constructor
    //广播接收器，更新歌曲信息 ,静态注册 ，Player.updateNowPlaying发送，另见NowPlayingActivity
    public static class UpdateSongReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(Player.UPDATE_SONG_INFO)) {
                LogUtils.d("action: " + intent.getExtras().getString(Player.EXTRA_ACTION));
                info = intent.getExtras().getParcelable(Player.EXTRA_INFO);
                art = null;
//            }
        }
    }
}
