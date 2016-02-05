package cc.haoduoyu.umaru.ui.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.CircleView;
import com.apkfuns.logutils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.BuildConfig;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;

/**
 * scroll: 所有想滚动出屏幕的view都需要设置这个flag- 没有设置这个flag的view将被固定在屏幕顶部。
 * enterAlways: 这个flag让任意向下的滚动都会导致该view变为可见，启用快速“返回模式”。
 * enterAlwaysCollapsed: 顾名思义，这个flag定义的是何时进入（已经消失之后何时再次显示）。
 * 假设你定义了一个最小高度（minHeight）同时enterAlways也定义了，那么view将在到达这个最小高度的时候开始显示，
 * 并且从这个时候开始慢慢展开，当滚动到顶部的时候展开完。
 * exitUntilCollapsed: 同样顾名思义，这个flag时定义何时退出，
 * 当你定义了一个minHeight，这个view将在滚动到达这个最小高度的时候消失。
 * Created by XP on 2016/2/3.
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.header)
    LinearLayout header;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.appbar)
    AppBarLayout mAppBar;
    @Bind(R.id.version)
    TextView mVersionTextView;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setVersionName();
        mCollapsingToolbarLayout.setTitle(getString(R.string.about));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setColorPrimary();
        header.setBackgroundColor(PreferencesUtils.getInteger(this, getString(R.string.color_primary), R.color.colorPrimary));
    }


    private void setVersionName() {
        mVersionTextView.setText("Version " + BuildConfig.VERSION_NAME);
    }

    protected void setColorPrimary() {
        int color = PreferencesUtils.getInteger(this, getString(R.string.color_primary), R.color.colorPrimary);
        mAppBar.setBackgroundColor(color);
        mCollapsingToolbarLayout.setContentScrimColor(color);
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CircleView.shiftColorDown(color));
            getWindow().setNavigationBarColor(color);
        }
    }

    @OnClick(R.id.version)
    void show() {
        LogUtils.d("a:" + SettingUtils.getInstance(this).isEnableAnimations());
        LogUtils.d(PreferencesUtils.getAll(this));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onResume() {
        super.onResume();
    }


    public void onPause() {
        super.onPause();
    }
}
