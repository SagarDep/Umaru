package cc.haoduoyu.umaru.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.apkfuns.logutils.LogUtils;

import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.volleyUtils.RequestManager;
import de.greenrobot.event.EventBus;

/**
 * Fragment基类
 * Created by XP on 2016/1/9.
 */
public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d("onResume");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        LogUtils.d("onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LogUtils.d("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d("onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy");
        EventBus.getDefault().unregister(this);
        RequestManager.cancelAll(this);

    }

    protected void executeRequest(Request<?> request) {
        RequestManager.addRequest(request, this);
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(error.getMessage());
            }
        };
    }


}