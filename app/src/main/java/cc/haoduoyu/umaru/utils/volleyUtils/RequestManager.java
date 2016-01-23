package cc.haoduoyu.umaru.utils.volleyUtils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apkfuns.logutils.LogUtils;

import cc.haoduoyu.umaru.Umaru;


/**
 * volley请求管理器
 * Created by XP on 2016/1/10.
 */
public class RequestManager {

    private static final String TAG = "RequestManager";
    public static RequestQueue mRequestQueue = Volley.newRequestQueue(Umaru.getContext());

    private RequestManager() {
    }

    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);

        LogUtils.d("addRequest = " + request.getUrl());

    }

    public static void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
    }
}
