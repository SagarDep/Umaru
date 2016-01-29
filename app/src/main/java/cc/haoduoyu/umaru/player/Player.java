package cc.haoduoyu.umaru.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.apkfuns.logutils.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.utils.PreferencesUtils;


/**
 * MediaPlayer播放器
 * http://developer.android.com/guide/topics/media/mediaplayer.html
 * Created by XP on 16/1/23.
 */
public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    //播放切换时发送播放歌曲的状态
    public static final String UPDATE_SONG_INFO = "cc.haoduoyu.umaru.player.UPDATE_SONG_INFO";
    public static final String EXTRA_INFO = "extra_info";
    public static final String EXTRA_ACTION = "extra_action";

    public static final String PREFERENCES_STATE = "state";

    //播放列表
    private ArrayList<Song> queue;
    private ArrayList<Song> shuffleQueue = new ArrayList<>();
    private int shuffleQueuePosition;
    private int queuePosition;
    private Context context;
    private Bitmap art;

    private int state;
    public static final int REPEAT_NONE = 0;//列表播放
    public static final int REPEAT_ALL = 1;//列表循环
    public static final int REPEAT_ONE = 2;//单曲循环
    public static final int SHUFFLE = 3;//随机播放

    //播放器
    private MediaPlayer mediaPlayer;

    /**
     * Player构造函数， 进行MediaPlayer初始化
     *
     * @param context
     */
    public Player(Context context) {
        this.context = context;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        queue = new ArrayList<>();
        queuePosition = 0;

        state = PreferencesUtils.getInteger(context, PREFERENCES_STATE, REPEAT_NONE);//默认state
        LogUtils.d("Player Construct, state:" + state);
    }

    /**
     * 播放前准备
     */
    public void begin() {
        mediaPlayer.stop();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(getNowPlaying().getSongData());
            mediaPlayer.prepareAsync(); //Prepares the player for playback, asynchronously.异步
            updateNowPlaying("begin");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放
     */
    public void play() {
        if (!isPlaying()) {
            mediaPlayer.start();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        mediaPlayer.pause();
    }

    /**
     * 播放和暂停切换
     */
    public void togglePlay() {
        if (isPlaying()) {
            pause();
        } else {
            play();
        }
        updateNowPlaying("togglePlay");
    }


    /**
     * 上一首
     */
    public void previous() {
        if (queuePosition - 1 < 0) {
            queuePosition = queue.size() - 1;
        } else {
            queuePosition--;
        }
        begin();
    }

    /**
     * 下一首
     */
    public void next() {
        if (state == SHUFFLE) {//随机
            if (shuffleQueuePosition + 1 < shuffleQueue.size()) {
                shuffleQueuePosition++;
                LogUtils.d("size:" + shuffleQueue.size() + "shuffleQueuePosition" + shuffleQueuePosition);
            } else {
                buildShuffleQueue();
            }
            begin();
        } else {//正常下一首
            if (queuePosition + 1 < queue.size()) {
                queuePosition++;
                LogUtils.d("size:" + queue.size() + "queuePosition" + queuePosition);
                begin();
            } else {
                if (state == REPEAT_ALL) {//全部重复
                    queuePosition = 0;
                    begin();
                } else {//正常最后一首
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                    updateNowPlaying("next");
                }
            }
        }
    }


    /**
     * 设置播放队列以及起始位置
     *
     * @param list
     * @param positon
     */
    public void setQueue(ArrayList<Song> list, int positon) {
        this.queue = list;
        this.queuePosition = positon;
        if (state == SHUFFLE) {
            buildShuffleQueue();
        }
    }

    public void setSeek(int progress) {
        if (progress <= mediaPlayer.getDuration() && getNowPlaying() != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    /**
     * 将一组歌添加到播放队列
     *
     * @param list
     */
    public void addQueue(ArrayList<Song> list) {
        this.queue.addAll(list);
    }

    /**
     * 准备完成
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();
        updateNowPlaying("onPrepared");
    }

    /**
     * 播放完成
     *
     * @param mediaPlayer
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (state == REPEAT_ONE) { //如果是单曲循环
            mediaPlayer.seekTo(0);
            play();
            updateNowPlaying("onCompletion");
        } else {
            next();
        }
    }

    /**
     * 结束时清除资源
     */
    public void finish() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        context = null;
    }


    /**
     * 设置播放状态，随机，列表循环，列表播放，单曲循环
     *
     * @param stateSetting
     */
    public void setPreferences(int stateSetting) {
        state = stateSetting;
        if (state == SHUFFLE) {
            if (shuffleQueue.size() == 0) {
                buildShuffleQueue();
            }
        } else {
            if (shuffleQueue.size() > 0) {
                queuePosition = queue.indexOf(shuffleQueue.get(shuffleQueuePosition));
                shuffleQueue = new ArrayList<>();
            }
        }
    }

    /**
     * 建立随机播放列表
     */
    private void buildShuffleQueue() {
        shuffleQueue.clear();

        if (queue.size() > 0) {
            shuffleQueuePosition = 0;
            shuffleQueue.add(queue.get(queuePosition));

            ArrayList<Song> random = new ArrayList<>();

            for (int i = 0; i < queuePosition; i++) {
                random.add(queue.get(i));
            }

            for (int i = queuePosition + 1; i < queue.size(); i++) {
                random.add(queue.get(i));
            }

            Collections.shuffle(random, new Random(System.nanoTime()));

            shuffleQueue.addAll(random);
        }
    }

    /**
     * 更新正在播放的音乐
     */
    private void updateNowPlaying(String action) {

        Intent intent = new Intent();
        intent.setAction(UPDATE_SONG_INFO);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_INFO, new PlayerInfo(this));
        bundle.putString(EXTRA_ACTION,action);
        intent.putExtras(bundle);
        context.sendOrderedBroadcast(intent, null);
        //发送更新音乐广播，第二个参数为设置权限，接收器具备相应权限才能正常接受广播，此处设null
        //PlayController接受
    }

    /**
     * 当前播放信息类
     */
    public static class PlayerInfo implements Parcelable {
        public boolean isPlaying;
        //存储当前系统时间
        public long currentTime;
        // 存储歌曲总时间
        public long duration;
        // 存储歌曲当前时间
        public long currentPosition;
        //当前播放队列
        public List<Song> queue;
        public int queuePosition;

        public PlayerInfo(Player player) {
            isPlaying = player.isPlaying();
            currentTime = System.currentTimeMillis();
            duration = player.getDuration();
            currentPosition = player.getCurrentPosition();
            queue = player.getQueue();
            queuePosition = player.getQueuePosition();
        }

        public PlayerInfo(Parcel parcel) {
            boolean[] booleans = new boolean[1];
            parcel.readBooleanArray(booleans);
            isPlaying = booleans[0];
            currentTime = parcel.readLong();
            duration = parcel.readLong();
            currentPosition = parcel.readLong();
            queue = parcel.createTypedArrayList(Song.CREATOR);
            queuePosition = parcel.readInt();
        }

        public static final Parcelable.Creator<PlayerInfo> CREATOR = new Parcelable.Creator<PlayerInfo>() {
            @Override
            public PlayerInfo createFromParcel(Parcel parcel) {
                return new PlayerInfo(parcel);
            }

            @Override
            public PlayerInfo[] newArray(int size) {
                return new PlayerInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flag) {
            parcel.writeBooleanArray(new boolean[]{isPlaying});
            parcel.writeLong(currentTime);
            parcel.writeLong(duration);
            parcel.writeLong(currentPosition);
            parcel.writeTypedArray(queue.toArray(new Parcelable[queue.size()]), flag);
            parcel.writeInt(queuePosition);
        }
    }

    /**
     * 判断是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 获得当前正在播放的歌曲
     *
     * @return
     */
    public Song getNowPlaying() {
        if (state == SHUFFLE) {
            if (shuffleQueue.size() == 0) {
                return null;
            } else {
                return shuffleQueue.get(shuffleQueuePosition);
            }
        } else {
            if (queue.size() == 0) {
                return null;
            } else {
                return queue.get(queuePosition);
            }
        }

    }


    //获得目前正在播放的时间
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }


    //获得当前播放队列位置
    public int getQueuePosition() {
        if (state == SHUFFLE) {
            return shuffleQueuePosition;
        }
        return queuePosition;
    }


    //获取当前播放队列
    public ArrayList<Song> getQueue() {
        if (state == SHUFFLE) {
            return new ArrayList<>(shuffleQueue);
        }
        return new ArrayList<>(queue);
    }


    //获取当前播放歌曲的总时间
    public long getDuration() {
        return getNowPlaying().getDuration();
    }

    public Bitmap getArt() {
        return art;
    }
}
