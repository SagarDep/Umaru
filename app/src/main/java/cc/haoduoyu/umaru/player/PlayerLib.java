package cc.haoduoyu.umaru.player;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import cc.haoduoyu.umaru.model.Song;

/**
 * 歌曲内容提供器
 * Created by XP on 16/1/23.
 */
public class PlayerLib {

    public static final List<Song> mSongLib = new ArrayList<>();

    /**
     * 扫描所有列表
     */
    public static void scanAll(Context context) {
        setSongLib(scanSongs(context));
    }

    /**
     * 扫描歌曲
     *
     * @return 歌曲列表
     */
    public static List<Song> scanSongs(Context context) {
        List<Song> songs = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            Song song = new Song(
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),//音乐ID
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),//音乐标题
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),//艺术家
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),//专辑
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)),//专辑ID
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),//时长
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)),//大小
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),//文件路径
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),//文件名字
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)));//MIME

            songs.add(song);
        }

        cursor.close();
        return songs;
    }

    /**
     * 设置歌曲列表
     */
    public static void setSongLib(List<Song> songList) {
        mSongLib.clear();
        mSongLib.addAll(songList);
    }

    /**
     * 获取歌曲列表
     */
    public static List<Song> getSongs() {
        return mSongLib;
    }


    public static boolean isEmpty() {
        boolean flag = false;

        if (mSongLib.isEmpty()) {
            flag = true;
        }

        return flag;
    }

}
