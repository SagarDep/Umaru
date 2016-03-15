package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;

import com.apkfuns.logutils.LogUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.Umaru;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.fragments.LocalMusicFragment;
import cc.haoduoyu.umaru.ui.fragments.MainFragment;
import cc.haoduoyu.umaru.ui.fragments.MusicFragment;
import cc.haoduoyu.umaru.utils.AppManager;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.ShakeManager;
import cc.haoduoyu.umaru.utils.SnackbarUtils;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.utils.zbar.CaptureActivity;
import cc.haoduoyu.umaru.widgets.FloatViewService;
import de.greenrobot.event.EventBus;

public class MainActivity extends ToolbarActivity implements NavigationView.OnNavigationItemSelectedListener, ShakeManager.ISensor {

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private static final String INDEX = "index";
    private BaseFragment mCurrentFragment;
    private Map<String, BaseFragment> mBaseFragmentByName = new HashMap<>();
    private FloatViewService mFloatViewService;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    public static void startIt(int index, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, MainActivity.class);
        intent.putExtra(INDEX, index);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();
        initViews();
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        initDrawer();
        setAppBarTransparent();
        initSnackBar();
        updateFloatView();

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

    private void initService() {
        //启动播放器服务,
        PlayerController.startService(this);
        //启动悬浮窗服务
        Intent intent = new Intent(this, FloatViewService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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
                else if (getString(R.string.umaru).equals(getTitle()) &&
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

    public void onEvent(MessageEvent event) {
        if (event.message.equals(MessageEvent.SHOW_OR_HIDE_FLOATVIEW)) {
            updateFloatView();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("isEnableShake: " + SettingUtils.getInstance(this).isEnableShake());
        if (SettingUtils.getInstance(this).isEnableShake())
            ShakeManager.getInstance(this).startShakeListener(this);
//        else
//            ShakeManager.getInstance(this).cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ShakeManager.getInstance(this).cancel();
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    private void destroy() {
        unbindService(mServiceConnection);
        EventBus.getDefault().unregister(this);
    }

    private void updateFloatView() {
        //bindService方法会异步执行,不延迟悬浮窗不会显示
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SettingUtils.getInstance(MainActivity.this).isEnableFloatView()) {
                    showFloatingView();
                } else {
                    hideFloatingView();
                }
            }
        }, 333);

    }

    /**
     * 显示悬浮图标
     */
    public void showFloatingView() {
        if (mFloatViewService != null) {
            mFloatViewService.showFloat();
        }
    }

    /**
     * 隐藏悬浮图标
     */
    public void hideFloatingView() {
        if (mFloatViewService != null) {
            mFloatViewService.hideFloat();
        }
    }

    /**
     * 连接到Service
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFloatViewService = ((FloatViewService.FloatViewServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mFloatViewService = null;
        }
    };

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                SettingActivity.startIt(this);
                break;
            case R.id.action_scan:
                CaptureActivity.startIt(this);
                break;
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
            case R.id.nav_day_night:
                dayNight();
                break;
            case R.id.nav_chat:
                ChatActivity.startIt(this);
                break;
            case R.id.nav_settings:
                SettingActivity.startIt(this);
                break;
            case R.id.nav_about:
                AboutActivity.startIt(this);
                break;
            case R.id.nav_exit:
                exit();
                break;

        }
        return true;
    }

    private void dayNight() {
        if (!PreferencesUtils.getBoolean(this, getString(R.string.night_yes), false)) {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            PreferencesUtils.setBoolean(this, getString(R.string.night_yes), true);
            SnackbarUtils.showShort(fab, getString(R.string.night_hint));
        } else {
//                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            PreferencesUtils.setBoolean(this, getString(R.string.night_yes), false);
            SnackbarUtils.showShort(fab, getString(R.string.day_hint));
        }
//                recreate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit();
//                final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
            }
        }, 1588);
    }

    private void exit() {
        destroy();
        stopService(new Intent(this, FloatViewService.class));
        PlayerController.stop();
        AppManager.getAppManager().finishAllActivityAndExit(this);
        System.exit(0);//完全退出application主题才会生效
    }

    /**
     * 缓存fragment
     *
     * @param fragmentName
     * @return
     */
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

    @Override
    public void onSensorChange(float force) {
        if (force > 50) {
            LogUtils.d("force: " + force);
            CaptureActivity.startIt(this);
        }
    }
}
