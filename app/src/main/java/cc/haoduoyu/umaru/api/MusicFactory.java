package cc.haoduoyu.umaru.api;


import com.apkfuns.logutils.LogUtils;

import retrofit2.Call;

/**
 * Created by XP on 2016/1/28.
 */
public class MusicFactory {


    private static final Object monitor = new Object();
    private static MusicService sMusicService = null;

    public static MusicService getMusicService() {
        synchronized (monitor) {
            if (sMusicService == null) {
                sMusicService = UmaruRetrofit.getInstance().getMusicService();
            }
            return sMusicService;
        }
    }
}
