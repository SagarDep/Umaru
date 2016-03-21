package cc.haoduoyu.umaru.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.apkfuns.logutils.LogUtils;

import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.utils.AppManager;
import cc.haoduoyu.umaru.utils.volley.RequestManager;

/**
 * Activity基类
 * Created by XP on 2016/1/9.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        LogUtils.d(getClass().getSimpleName() + " onCreate");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        RequestManager.cancelAll(this);
        LogUtils.d(getClass().getSimpleName() + " onDestroy");


// 在发出请求前调用Request的setTag()方法为每个请求加一个标签，这个方法的参数是Object，
// 所以我们可以使用任何类型作为标签。这样就可以调用ReqiestQueue的cancelAll()函数取消一群标签了。
// 比较常用的方法就是，将发出这个请求的Activity或者Fragment作为标签，并在onStop()中调用cancelAll()
    }

    public void replaceFragmentWithSelected(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragment);
//        fragmentTransaction.addToBackStack(null);//带返回栈
        fragmentTransaction.commit();

    }

    public void replaceFragment(int id_content, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id_content, fragment, "tag");
        transaction.commit();
    }

    Fragment mContent;

    //会导致视图叠加
    public void switchFragment(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.frame_content, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    protected void executeRequest(Request<?> request) {
        RequestManager.addRequest(request, this);
    }


}
