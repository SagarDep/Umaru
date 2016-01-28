package cc.haoduoyu.umaru.ui.fragments.music;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.adapter.SongAdapter;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.event.MessageEvent;

/**
 * Created by XP on 2016/1/25.
 */
public class LocalMusicFragment extends BaseFragment {


    @Bind(R.id.song_list)
    RecyclerView songList;

    private SongAdapter mAdapter;

    public static LocalMusicFragment newInstance() {
        LocalMusicFragment fragment = new LocalMusicFragment();
        return fragment;
    }

    @Override
    protected void initViews() {

        mAdapter = new SongAdapter(getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        songList.setLayoutManager(layoutManager);
        songList.setAdapter(mAdapter);
        mAdapter.setList(PlayerLib.getSongs());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int provideLayoutId() {
        return R.layout.fragment_local_music;
    }

    public void onEvent(MessageEvent event) {

    }
}