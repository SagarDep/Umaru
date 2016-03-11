package cc.haoduoyu.umaru.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.BuildConfig;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.Utils;

/**
 * https://github.com/ksoichiro/Android-ObservableScrollView
 * Created by XP on 2016/2/3.
 */
public class AboutActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    @Bind(R.id.toolbar)
    Toolbar mToolbarView;
    @Bind(R.id.image)
    ImageView mImageView;
    @Bind(R.id.scroll)
    ObservableScrollView mScrollView;

    private int mParallaxImageHeight;

    public static void startIt(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbarView);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = Utils.dpToPx(280);
        Glide.with(this).load(Constants.PIC_URL + (int) (Math.random() * 21 + 1) + ".jpg").crossFade().into(mImageView);

        if (SettingUtils.getInstance(this).isEnableAnimations())
            startToolbarAnimation();
    }

    @OnClick(R.id.icon)
    void version() {
        Toast.makeText(this, "      " + getString(R.string.version)
                + BuildConfig.VERSION_NAME
                + "\n\n" + PreferencesUtils.getAll(this)
                + "\n\n" + SettingUtils.getAll(), Toast.LENGTH_LONG).show();
        LogUtils.d(PreferencesUtils.getAll(this));
        LogUtils.d(SettingUtils.getAll());
    }

    @OnClick(R.id.image)
    void show() {
        Glide.with(this).load(Constants.PIC_URL + (int) (Math.random() * 21 + 1) + ".jpg").crossFade().into(mImageView);
    }

    @OnClick(R.id.developer)
    void develop() {
        WebViewActivity.startIt(this, Constants.HAO_DUO_YU, null);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = PreferencesUtils.getInteger(this, getString(R.string.color_primary), R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
        LogUtils.d("scrollY: " + scrollY + " firstScroll: " + firstScroll + " dragging: " + dragging);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    protected void startToolbarAnimation() {
        int size = Utils.dpToPx(81);
        mToolbarView.setTranslationY(-size);
        mToolbarView.animate()
                .translationY(0)
                .setDuration(500)
                .setStartDelay(300);
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
}
