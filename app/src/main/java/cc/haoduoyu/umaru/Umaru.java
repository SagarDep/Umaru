package cc.haoduoyu.umaru;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import cc.haoduoyu.umaru.player.Player;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.player.PlayerLib;

/**
 * Created by XP on 2016/1/10.
 */
public class Umaru extends Application {
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        PlayerController.startService(this);
        PlayerLib.scanAll(this);
    }
}
