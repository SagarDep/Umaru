package cc.haoduoyu.umaru.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.fragments.MainFragment;
import cc.haoduoyu.umaru.ui.fragments.MusicFragment;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SnackbarUtils;
import cc.haoduoyu.umaru.utils.Utils;
import de.greenrobot.event.EventBus;

public class MainActivity extends ToolbarActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private BaseFragment mCurrentFragment;
    private Map<String, BaseFragment> mBaseFragmentByName = new HashMap<>();

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        mCurrentFragment = getFragment(MainFragment.class.getName());
        replaceFragment(R.id.frame_content, mCurrentFragment);
    }

    /**
     * 初始化View
     */
    private void initViews() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        startToolbarAnimation();
        startFabAnimation();
        setAppBarTransparent();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SnackbarUtils.showSnackBackWithAction(fab, "点击上面的圆形按钮与我聊聊吧~", "OK");
            }
        }, 1558);
        replaceFragment(R.id.frame_content, new MainFragment());
    }

    protected void startFabAnimation() {
        fab.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.fab_size));
        fab.animate().translationY(0).setInterpolator(new OvershootInterpolator(1.f)).setStartDelay(400).setDuration(500);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_night:
                Constants.isDay = !Constants.isDay;
                PreferencesUtils.setBoolean(this, "w_pic", Constants.isDay);
                EventBus.getDefault().post(new MessageEvent("pic"));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_index:
                mToolbar.setTitle(getResources().getString(R.string.umaru));
                fab.setImageResource(android.R.drawable.stat_notify_chat);
                mCurrentFragment = getFragment(MainFragment.class.getName());
                replaceFragment(R.id.frame_content, mCurrentFragment);

                break;
            case R.id.nav_music:
                mToolbar.setTitle(getResources().getString(R.string.music));
                fab.setImageResource(android.R.drawable.ic_media_play);
                mCurrentFragment = getFragment(MusicFragment.class.getName());
                replaceFragment(R.id.frame_content, mCurrentFragment);

                break;
            case R.id.nav_settings:

                break;
            case R.id.nav_about:
                break;
            case R.id.nav_help:
                break;
        }


        return true;
    }


    private BaseFragment getFragment(String fragmentName) {
        BaseFragment baseFragment = mBaseFragmentByName.get(fragmentName);
        if (baseFragment == null) {
            try {
                baseFragment = (BaseFragment) Class.forName(fragmentName).newInstance();
                //为null时new指定name的fragment
            } catch (Exception e) {
                baseFragment = MainFragment.newInstance();
            }
            mBaseFragmentByName.put(fragmentName, baseFragment);
        }
        return baseFragment;
    }

    @OnClick(R.id.fab)
    public void onStartIt() {
        if (getResources().getString(R.string.umaru).equals(mToolbar.getTitle())) {
            int[] startingLocation = new int[2];
            fab.getLocationOnScreen(startingLocation);
            startingLocation[0] += fab.getWidth() / 2;
            ChatActivity.startIt(startingLocation, this);
            overridePendingTransition(0, 0);
        } else if (PlayerController.getNowPlaying() != null) {
            NowPlayingActivity.startIt(PlayerController.getNowPlaying(), this);
        } else if (PlayerLib.getSongs().size() != 0) {
            PlayerController.setQueueAndPosition(PlayerLib.getSongs(), 0);
            PlayerController.begin();
            NowPlayingActivity.startIt(PlayerLib.getSongs().get(0), this);
        } else {
            SnackbarUtils.showShort(fab, getString(R.string.fab_no_music));
        }
    }
}
