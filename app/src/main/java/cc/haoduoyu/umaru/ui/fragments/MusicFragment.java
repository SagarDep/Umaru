package cc.haoduoyu.umaru.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.utils.PreferencesUtils;

/**
 * Created by XP on 2016/1/9.
 */
public class MusicFragment extends BaseFragment {

    //不要让客户端去调用默认的构造函数，然后手动地设置fragment的参数。我们直接为它们提供一个静态工厂方法。
    // 这样做比调用默认构造方法好，有两个原因：一个是，它方便别人的调用。另一个是，保证了fragment的构建过程不会出错。
    //通过提供一个静态工厂方法，我们避免了自己犯错--我们再也不用担心不小心忘记初始化fragmnet的参数或者没正确设置参数。
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    public static MusicFragment newInstance() {
        MusicFragment fragment = new MusicFragment();
        return fragment;
    }

    @Override
    protected void initViews() {
        int color = PreferencesUtils.getInteger(getActivity(), getString(R.string.color_primary), R.color.colorPrimary);
        iv.setBackgroundColor(color);
        tabLayout.setBackgroundColor(color);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.setOffscreenPageLimit(3);
        }
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int provideLayoutId() {
        return R.layout.fragment_music;
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(OnlineFragment.newInstance(0), getActivity().getString(R.string.chart));
        adapter.addFragment(OnlineFragment.newInstance(1), getActivity().getString(R.string.chart_artists));
        adapter.addFragment(LocalMusicFragment.newInstance(), getActivity().getString(R.string.local_music));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                PlayerLib.scanAll(getActivity());
            }
        }).start();
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    public void onEvent(MessageEvent event) {

    }


}
