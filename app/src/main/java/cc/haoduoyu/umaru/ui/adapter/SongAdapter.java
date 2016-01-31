package cc.haoduoyu.umaru.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.model.Song;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.ui.activities.NowPlayingActivity;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.widgets.CircleImageView;

/**
 * Created by XP on 2016/1/23.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private Context mContext;
    private List<Song> mSongList;

    public SongAdapter(Context context) {
        mContext = context;
        mSongList = new ArrayList<>();
    }

    public void setList(List<Song> list) {
        mSongList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Song s = mSongList.get(position);

        holder.mSongImage.setImageResource(R.mipmap.default_artwork);
        holder.mSongTitle.setText(s.getSongTitle());
        holder.mSongArtist.setText(s.getArtistName());
        holder.mSongDuration.setText(Utils.durationToString(s.getDuration()));

        holder.mSongRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerController.setQueueAndPosition(mSongList, position);//传递歌曲列表和点击位置
                PlayerController.begin();
                NowPlayingActivity.startIt(s, (Activity) mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.song_root)
        RelativeLayout mSongRoot;
        @Bind(R.id.song_image)
        CircleImageView mSongImage;
        @Bind(R.id.song_title)
        TextView mSongTitle;
        @Bind(R.id.song_artist)
        TextView mSongArtist;
        @Bind(R.id.song_duration)
        TextView mSongDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
