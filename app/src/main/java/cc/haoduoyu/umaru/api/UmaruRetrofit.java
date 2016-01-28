package cc.haoduoyu.umaru.api;

import java.util.concurrent.TimeUnit;


import okhttp3.OkHttpClient;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * http://square.github.io/retrofit/
 * Created by XP on 2016/1/25.
 */
public class UmaruRetrofit {
    private static UmaruRetrofit instance;

    private MusicService musicService;

    public static UmaruRetrofit getInstance() {
        if (instance == null)
            instance = new UmaruRetrofit();
        return instance;
    }

    private UmaruRetrofit() {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.setReadTimeout(7676, TimeUnit.MILLISECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MusicService.LAST_FM_URL)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .client(okHttpClient)
                .build();
        musicService = retrofit.create(MusicService.class);
    }

    public MusicService getMusicService() {
        return musicService;
    }


}