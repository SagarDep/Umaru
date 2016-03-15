package cc.haoduoyu.umaru.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Toast.makeText(this, getString(R.string.version)
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

    @OnClick(R.id.crash)
    void crash() {
//        throw new RuntimeException("crash crash crash crash crash");
        sendPost(Constants.WZ_URL);
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

    public CookieManager cookieManager = null;
    public static String cookies;

    /**
     * IDButton:Submit
     * encoded:false
     * goto:
     * gx_charset:UTF-8
     * IDToken0:
     * IDToken1:1217443023
     * IDToken9:
     * IDToken2:ec5bb526f85028e09d4449ac86c3fea5
     *
     * @param url
     * @return
     */
    public String sendPost(String url) {
        CookieSyncManager.createInstance(this);
        // 每次登录操作的时候先清除cookie
        removeAllCookie();
        // 根据url获得HttpPost对象
        HttpPost httpRequest = new HttpPost(url);
        // 取得默认的HttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String strResult = null;
        // NameValuePair实现请求参数的封装
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("IDButton", "Submit"));
        params.add(new BasicNameValuePair("encoded", "false"));
        params.add(new BasicNameValuePair("goto", ""));
        params.add(new BasicNameValuePair("gx_charset", "UTF-8"));
        params.add(new BasicNameValuePair("IDToken0", ""));
        params.add(new BasicNameValuePair("IDToken1", "1217443023"));
        params.add(new BasicNameValuePair("IDToken9", "080019"));
        params.add(new BasicNameValuePair("IDToken2", "ec5bb526f85028e09d4449ac86c3fea5"));
//        httpRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpRequest.addHeader("Cookie", cookies);
        httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpRequest.addHeader("Origin", "http://ids1.suda.edu.cn");
        httpRequest.addHeader("Referer", "http://ids1.suda.edu.cn/amserver/UI/Login?goto=http://myauth.suda.edu.cn/default.aspx?app=wzjw");
        try {
            // 添加请求参数到请求对象
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            // 获得响应对象
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            // 判断是否请求成功
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 获得响应返回Json格式数据
                strResult = EntityUtils.toString(httpResponse.getEntity());
                // 取得Cookie
                CookieStore mCookieStore = httpclient.getCookieStore();
                List<Cookie> cookies = mCookieStore.getCookies();
                if (cookies.isEmpty()) {
                    System.out.println("Cookies为空");
                } else {
                    for (int i = 0; i < cookies.size(); i++) {
                        // 保存cookie
                        Cookie cookie = cookies.get(i);
                        LogUtils.d("Cookie" + cookies.get(i).getName() + "=" + cookies.get(i).getValue());
                        cookieManager = CookieManager.getInstance();
                        String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
                        cookieManager.setCookie(url, cookieString);
                        PreferencesUtils.setString(this, getString(R.string.cookie), cookieString);
                    }
                }
                return strResult;
            } else {
                strResult = "错误响应:" + httpResponse.getStatusLine().toString();
            }
        } catch (ClientProtocolException e) {
            strResult = "错误响应:" + e.getMessage();
            e.printStackTrace();
            return strResult;
        } catch (IOException e) {
            strResult = "错误响应:" + e.getMessage();
            e.printStackTrace();
            return strResult;
        } catch (Exception e) {
            strResult = "错误响应:" + e.getMessage();
            e.printStackTrace();
            return strResult;
        }
        return strResult;
    }

    private void removeAllCookie() {
        cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }
}
