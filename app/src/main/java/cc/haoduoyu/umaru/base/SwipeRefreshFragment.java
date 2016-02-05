package cc.haoduoyu.umaru.base;

import android.support.v4.widget.SwipeRefreshLayout;

import butterknife.Bind;
import cc.haoduoyu.umaru.R;

/**
 * Created by XP on 2016/1/31.
 */
public abstract class SwipeRefreshFragment extends BaseFragment {

    @Bind(R.id.swipeRefreshLayout)
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRequestDataRefresh = false;


    @Override
    protected void initViews() {
        trySetupSwipeRefresh();
    }

    void trySetupSwipeRefresh() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            mSwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            requestDataRefresh();
                        }
                    });
        }
    }


    public void requestDataRefresh() {
        mIsRequestDataRefresh = true;
    }


    public void setRequestDataRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {//如果不需要刷新
            mIsRequestDataRefresh = false;
            // 防止刷新消失太快
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1358);
        } else {//如果需要刷新
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    public boolean isRequestDataRefresh() {
        return mIsRequestDataRefresh;
    }
}
