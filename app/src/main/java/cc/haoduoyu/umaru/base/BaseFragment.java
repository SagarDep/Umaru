package cc.haoduoyu.umaru.base;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.apkfuns.logutils.LogUtils;

import butterknife.ButterKnife;
import cc.haoduoyu.umaru.utils.volley.RequestManager;
import de.greenrobot.event.EventBus;

/**
 * Fragment基类
 * Created by XP on 2016/1/9.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract void initViews();

    protected abstract int provideLayoutId();

    protected View rootView;

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(getClass().getSimpleName() + " onResume");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        LogUtils.d(getClass().getSimpleName() + " onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (rootView == null || provideLayoutId() == R.layout.fragment_music) { //java.lang.IllegalStateException: Activity has been destroyed
        rootView = inflater.inflate(provideLayoutId(), container, false);
//        }
        LogUtils.d(getClass().getSimpleName() + " onCreateView");
        ButterKnife.bind(this, rootView);
        initViews();

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d(getClass().getSimpleName() + " onViewCreated");

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.d(getClass().getSimpleName() + " onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(getClass().getSimpleName() + " onDestroy");
        EventBus.getDefault().unregister(this);
        RequestManager.cancelAll(this);

    }

    protected void executeRequest(Request<?> request) {
        RequestManager.addRequest(request, this);
    }
}