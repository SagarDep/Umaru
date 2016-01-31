package cc.haoduoyu.umaru.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.api.MusicFactory;
import cc.haoduoyu.umaru.model.TopArtists;
import cc.haoduoyu.umaru.model.TopTracks;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.widgets.RatioImageView;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by XP on 2016/1/31.
 */
public class OnlineMusicAdapter extends RecyclerView.Adapter<OnlineMusicAdapter.ViewHolder> {

    private Context mContext;
    private List<TopTracks.TracksEntity.TrackEntity> mTrackList;
    private List<TopArtists.ArtistsEntity.ArtistEntity> mArtistList;
    private int page;
    private int limit = 10;
    private int type;

    public OnlineMusicAdapter(Context context, int type) {
        mContext = context;
        this.type = type;
        mTrackList = new ArrayList<>();
        mArtistList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        if (type == 0) {
            holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_top_tracks, parent, false));
            if (viewType == TopTracks.TracksEntity.TrackEntity.HEADER) {
//            holder = new ViewHolder(LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_top_img, parent, false));
            }
        } else if (type == 1) {
            holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_top_tracks, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (type == 0) {
            final TopTracks.TracksEntity.TrackEntity t = mTrackList.get(position);
            Glide.with(mContext).load(t.getImage().get(3).getText())
                    .into(holder.mImageView);
            holder.mSongName.setText(t.getName());
            holder.mSongText.setText(t.getArtist().getName());
            holder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast("click" + position);
                }
            });
        } else if (type == 1) {
            final TopArtists.ArtistsEntity.ArtistEntity a = mArtistList.get(position);
            Glide.with(mContext).load(a.getImage().get(3).getText())
                    .into(holder.mImageView);
            holder.mSongName.setText(a.getName());
            holder.mSongText.setText(a.getListeners());
            holder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast("click" + position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (type == 0)
            return mTrackList.size();
        return mArtistList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TopTracks.TracksEntity.TrackEntity.HEADER;
        return TopTracks.TracksEntity.TrackEntity.DEFAULT;
    }

    public void loadFirst(int type) {
        page = 1;
        if (type == 0) {
            loadTracks();
        } else if (type == 1) {
            loadArtists();
        }
    }

    public void loadNextPage() {
        page++;
        if (type == 0) {
            loadTracks();
        } else if (type == 1) {
            loadArtists();
        }
    }

    private void loadTracks() {

        MusicFactory.getMusicService().getTopTracks(page, limit).enqueue(new Callback<TopTracks>() {
            @Override
            public void onResponse(Response<TopTracks> response) {

                if (page == 1) {
                    mTrackList.clear();
                }
                mTrackList.addAll(response.body().getTracks().getTrack());
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                LogUtils.e(t.getMessage());
            }
        });
    }

    private void loadArtists() {

        MusicFactory.getMusicService().getTopArtists(page, limit).enqueue(new Callback<TopArtists>() {
            @Override
            public void onResponse(Response<TopArtists> response) {

                if (page == 1) {
                    mArtistList.clear();
                }
                mArtistList.addAll(response.body().getArtists().getArtist());
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
                LogUtils.e(t.getMessage());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.cardView)
        LinearLayout mCard;
        @Bind(R.id.song_image)
        RatioImageView mImageView;
        @Bind(R.id.song_name)
        TextView mSongName;
        @Bind(R.id.song_text)
        TextView mSongText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mImageView.setOriginalSize(55, 50 + new Random().nextInt(10));
        }
    }
}