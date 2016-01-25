package cc.haoduoyu.umaru.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;


import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.db.DBHelper;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.model.City;
import cc.haoduoyu.umaru.fragments.MainFragment;
import cc.haoduoyu.umaru.fragments.MusicFragment;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SnackbarUtils;
import de.greenrobot.event.EventBus;

public class MainActivity extends ToolbarActivity implements NavigationView.OnNavigationItemSelectedListener {


    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
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
        replaceFragment(R.id.frame_content, MainFragment.newInstance());
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

        if (id == R.id.action_settings) {
            return true;
        }
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_index:
//                fab.setVisibility(View.VISIBLE);
                mToolbar.setTitle(getResources().getString(R.string.umaru));
                setAppBarTransparent();

                replaceFragment(R.id.frame_content, MainFragment.newInstance());
                break;
            case R.id.nav_music:
//                fab.setVisibility(View.GONE);
                mToolbar.setTitle(getResources().getString(R.string.music));
                setAppBarColorful();

                replaceFragment(R.id.frame_content, MusicFragment.newInstance());

                break;
            case R.id.nav_settings:

                break;
            case R.id.nav_about:
                break;
            case R.id.nav_help:
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @OnClick(R.id.fab)
    public void onStartChatClick() {
        //just for test
//        new CityDao(getApplicationContext()).deleteAll();
        DBHelper.getHelper(getApplicationContext()).clearTable(City.class);

        int[] startingLocation = new int[2];
        fab.getLocationOnScreen(startingLocation);
        startingLocation[0] += fab.getWidth() / 2;
        ChatActivity.startIt(startingLocation, this);
        overridePendingTransition(0, 0);
    }
}
