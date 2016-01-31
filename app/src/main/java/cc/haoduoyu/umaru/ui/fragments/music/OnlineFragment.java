package cc.haoduoyu.umaru.ui.fragments.music;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import butterknife.Bind;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.SwipeRefreshFragment;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.ui.adapter.OnlineMusicAdapter;

/**
 * Created by XP on 2016/1/25.
 */
public class OnlineFragment extends SwipeRefreshFragment {

    @Bind(R.id.online_list)
    RecyclerView mOnlineList;

    OnlineMusicAdapter mAdapter;
    int type;

    /**
     * Activity重新创建时，会重新构建它所管理的Fragment，原先的Fragment的字段值将会全部丢失，
     * 但是通过 Fragment.setArguments(Bundle bundle)方法设置的bundle会保留下来。
     * 所以尽量使用 Fragment.setArguments(Bundle bundle)方式来传递参数
     *
     * @return
     */
    public static OnlineFragment newInstance(int type) {
        OnlineFragment fragment = new OnlineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews() {
        super.initViews();
        type = getArguments().getInt("type");
        mAdapter = new OnlineMusicAdapter(getActivity(), type);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mOnlineList.setLayoutManager(layoutManager);
        mOnlineList.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRequestDataRefresh(true);
            }
        }, 388);
        if (type == 0) {
            mAdapter.loadFirst(0);
        } else if (type == 1) {
            mAdapter.loadFirst(1);
        }
        setRequestDataRefresh(false);
    }

    @Override
    protected int provideLayoutId() {
        return R.layout.fragment_online;
    }


    @Override
    public void requestDataRefresh() {
        super.requestDataRefresh();
        if (type == 0) {
            mAdapter.loadFirst(0);
        } else if (type == 1) {
            mAdapter.loadFirst(1);
        }
        setRequestDataRefresh(false);
    }

    public void onEvent(MessageEvent event) {

    }
}
