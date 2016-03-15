package cc.haoduoyu.umaru.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.api.MusicFactory;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.model.TopArtists;
import cc.haoduoyu.umaru.model.TopTracks;
import cc.haoduoyu.umaru.ui.activities.WebViewActivity;
import cc.haoduoyu.umaru.utils.SettingUtils;
import de.greenrobot.event.EventBus;
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
    private int limit = 15;
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
            int picQuality = Integer.parseInt(SettingUtils.getInstance(mContext).getPicQuality());
//            LogUtils.d(picQuality);
            Glide.with(mContext).load(t.getImage().get(picQuality).getText())
                    .into(holder.mImageView);
            holder.mSongName.setText(t.getName());
            holder.mSongText.setText(t.getArtist().getName());
            holder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.startIt(mContext, t.getUrl(), null);
                }
            });
        } else if (type == 1) {
            final TopArtists.ArtistsEntity.ArtistEntity a = mArtistList.get(position);
            int picQuality = Integer.parseInt(SettingUtils.getInstance(mContext).getPicQuality());
            if (position == 0)
                LogUtils.d(picQuality);
            Glide.with(mContext).load(a.getImage().get(picQuality).getText())
                    .into(holder.mImageView);
            holder.mSongName.setText(a.getName());
            holder.mSongText.setText(a.getListeners());
            holder.mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebViewActivity.startIt(mContext, a.getUrl(), null);
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

    public void loadNextPage(int type) {
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
                if (page == 1) mTrackList.clear();
                if (response.body() != null)
                    mTrackList.addAll(response.body().getTracks().getTrack());
                notifyDataSetChanged();

                EventBus.getDefault().post(new MessageEvent(MessageEvent.LOAD_DONE));
            }

            @Override
            public void onFailure(Throwable t) {
                LogUtils.e(t.toString());
                EventBus.getDefault().post(new MessageEvent(MessageEvent.LOAD_DONE));
            }
        });
    }

    private void loadArtists() {

        MusicFactory.getMusicService().getTopArtists(page, limit).enqueue(new Callback<TopArtists>() {
            @Override
            public void onResponse(Response<TopArtists> response) {
                if (page == 1) mArtistList.clear();
                if (response.body() != null)
                    mArtistList.addAll(response.body().getArtists().getArtist());
                notifyDataSetChanged();

                EventBus.getDefault().post(new MessageEvent(MessageEvent.LOAD_DONE));
            }

            @Override
            public void onFailure(Throwable t) {
                LogUtils.e(t.toString());
                EventBus.getDefault().post(new MessageEvent(MessageEvent.LOAD_DONE));
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.cardView)
        LinearLayout mCard;
        @Bind(R.id.song_image)
        ImageView mImageView;
        @Bind(R.id.song_name)
        TextView mSongName;
        @Bind(R.id.song_text)
        TextView mSongText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}