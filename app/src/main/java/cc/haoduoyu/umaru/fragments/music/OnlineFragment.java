package cc.haoduoyu.umaru.fragments.music;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.adapter.SongAdapter;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.event.MessageEvent;

/**
 * Created by XP on 2016/1/25.
 */
public class OnlineFragment extends BaseFragment {
    /**
     * Activity重新创建时，会重新构建它所管理的Fragment，原先的Fragment的字段值将会全部丢失，
     * 但是通过 Fragment.setArguments(Bundle bundle)方法设置的bundle会保留下来。
     * 所以尽量使用 Fragment.setArguments(Bundle bundle)方式来传递参数
     *
     * @return
     */
    public static OnlineFragment newInstance() {
        OnlineFragment fragment = new OnlineFragment();
        return fragment;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_online;
    }


    public void onEvent(MessageEvent event) {

    }
}
