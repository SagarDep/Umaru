package cc.haoduoyu.umaru.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.utils.StatusBarCompat;
import cc.haoduoyu.umaru.utils.Utils;

/**
 * 包含Toolbar的Fragment基类
 * Created by XP on 2016/1/25.
 */
public abstract class ToolbarFragment extends BaseFragment {

    @Bind(R.id.app_bar_layout)
    protected AppBarLayout mAppBar;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;


    public void onToolbarClick() {
    }

    @Override
    protected void initViews() {
        initToolbar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
    }

    private void initToolbar() {
        if (mToolbar == null || mAppBar == null) {
            throw new IllegalStateException("No Toolbar");
        }
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarClick();
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

    protected void setAppBarColorful() {
        mAppBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    protected Toolbar getToolbar() {
        return mToolbar;
    }
}
