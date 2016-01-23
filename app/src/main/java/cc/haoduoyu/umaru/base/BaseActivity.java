package cc.haoduoyu.umaru.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import cc.haoduoyu.umaru.utils.AppManager;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.volleyUtils.RequestManager;
import de.greenrobot.event.EventBus;

/**
 * Activity基类
 * Created by XP on 2016/1/9.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        RequestManager.cancelAll(this);
//        DBHelper.getHelper(getApplicationContext()).close();//cause fc

// 在发出请求前调用Request的setTag()方法为每个请求加一个标签，这个方法的参数是Object，
// 所以我们可以使用任何类型作为标签。这样就可以调用ReqiestQueue的cancelAll()函数取消一群标签了。
// 比较常用的方法就是，将发出这个请求的Activity或者Fragment作为标签，并在onStop()中调用cancelAll()
    }

    protected void replaceFragmentWithSelected(Fragment fragment) {

        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void replaceFragment(int id_content, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id_content, fragment);
        transaction.commit();
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
