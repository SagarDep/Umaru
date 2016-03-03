package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.Umaru;
import cc.haoduoyu.umaru.api.MusicFactory;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.fragments.LocalMusicFragment;
import cc.haoduoyu.umaru.ui.fragments.MainFragment;
import cc.haoduoyu.umaru.ui.fragments.MusicFragment;
import cc.haoduoyu.umaru.ui.fragments.OnlineFragment;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
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

    private static final String INDEX = "index";
    private BaseFragment mCurrentFragment;
    private Map<String, BaseFragment> mBaseFragmentByName = new HashMap<>();

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    public static void startIt(int index, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MainActivity.class);
        intent.putExtra(INDEX, index);//启动动画
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    private void initViews() {
        initDrawer();
        setAppBarTransparent();
        initSnackBar();

        if (SettingUtils.getInstance(this).isEnableAnimations()) {
            startToolbarAnimation();
            startFabAnimation();
        }

        if (getIntent().getIntExtra(INDEX, 0) == 1) {
            setTitle(getResources().getString(R.string.music));
            fab.setImageResource(android.R.drawable.ic_media_play);
            mCurrentFragment = getFragment(MusicFragment.class.getName());
        } else {
            mCurrentFragment = getFragment(MainFragment.class.getName());
        }
        replaceFragmentWithSelected(mCurrentFragment);
    }

    /**
     * 初始化SnackBar
     */
    private void initSnackBar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Utils.isNetworkReachable(Umaru.getContext()))
                    SnackbarUtils.showLong(fab, getString(R.string.snackbar_error));
                else if (getString(R.string.umaru).equals(mToolbar.getTitle()) &&
                        !SettingUtils.getInstance(MainActivity.this).isEnableChatGuide())
                    SnackbarUtils.showSnackBackWithAction(fab, getString(R.string.snackbar_chat), getString(R.string.know));
            }
        }, 1558);
    }

    /**
     * 初始化Drawer
     */
    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 启动FAB动画
     */
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
                SettingActivity.startIt(this);
                break;
//            case R.id.action_night:
//                Constants.isDay = !Constants.isDay;
//                PreferencesUtils.setBoolean(this, "w_pic", Constants.isDay);
//                EventBus.getDefault().post(new MessageEvent(MessageEvent.WEATHER_PIC));
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_index:
                setTitle(getResources().getString(R.string.umaru));
                fab.setImageResource(android.R.drawable.stat_notify_chat);
                mCurrentFragment = getFragment(MainFragment.class.getName());
                replaceFragmentWithSelected(mCurrentFragment);
                break;
            case R.id.nav_music:
                setTitle(getResources().getString(R.string.music));
                fab.setImageResource(android.R.drawable.ic_media_play);
                mCurrentFragment = getFragment(MusicFragment.class.getName());
                replaceFragmentWithSelected(mCurrentFragment);
                break;
            case R.id.nav_settings:
                SettingActivity.startIt(this);
                break;
            case R.id.nav_about:
                AboutActivity.startIt(this);
                break;
        }
        return true;
    }


    private BaseFragment getFragment(String fragmentName) {
        BaseFragment baseFragment = mBaseFragmentByName.get(fragmentName);
        if (mBaseFragmentByName.get(fragmentName) == null) {
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
        if (getString(R.string.umaru).equals(mToolbar.getTitle())) {
            int[] startingLocation = new int[2];
            fab.getLocationOnScreen(startingLocation);
            startingLocation[0] += fab.getWidth() / 2;
            ChatActivity.startIt(startingLocation, this);
            if (SettingUtils.getInstance(this).isEnableAnimations())
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
