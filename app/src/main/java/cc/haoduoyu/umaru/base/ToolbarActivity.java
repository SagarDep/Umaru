package cc.haoduoyu.umaru.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.utils.StatusBarCompat;
import cc.haoduoyu.umaru.utils.Utils;

/**
 * 包含Toolbar的Activity基类
 * Created by XP on 2016/1/9.
 */
public abstract class ToolbarActivity extends BaseActivity {

    abstract protected int provideContentViewId();

    public void onToolbarClick() {
    }

    @Bind(R.id.app_bar_layout)
    protected AppBarLayout mAppBar;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(provideContentViewId());
//        mAppBar = (AppBarLayout) findViewById(R.id.include).findViewById(R.id.app_bar_layout);
//        mToolbar = (Toolbar) findViewById(R.id.include).findViewById(R.id.toolbar);
        ButterKnife.bind(this);
        if (mToolbar == null || mAppBar == null) {
            throw new IllegalStateException("No Toolbar");
        }
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarClick();
            }
        });

        setSupportActionBar(mToolbar);
        if (canBack()) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);//给左上角图标的左边加上一个返回的图标
        }

        StatusBarCompat.compat(this);//状态栏变色
        startToolbarAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//返回键
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * toolbar动画
     */
    protected void startToolbarAnimation() {
        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getToolbar().animate()
                .translationY(0)
                .setDuration(500)
                .setStartDelay(300);
    }

    protected void setAppBarAlpha(float alpha) {
        mAppBar.setAlpha(alpha);
    }

    protected void setAppBarTransparent() {
        mAppBar.setBackgroundColor(Color.TRANSPARENT);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
    }

    protected boolean canBack() {
        return false;
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }
}
