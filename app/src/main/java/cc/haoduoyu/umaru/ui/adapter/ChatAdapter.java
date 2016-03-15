package cc.haoduoyu.umaru.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.model.Chat;
import cc.haoduoyu.umaru.player.PlayerController;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.activities.ChatActivity;
import cc.haoduoyu.umaru.ui.activities.MainActivity;
import cc.haoduoyu.umaru.ui.activities.NowPlayingActivity;
import cc.haoduoyu.umaru.ui.activities.WebViewActivity;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.ToastUtils;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.utils.volley.GsonRequest;
import cc.haoduoyu.umaru.utils.volley.RequestManager;
import cc.haoduoyu.umaru.utils.zbar.CaptureActivity;

/**
 * Created by XP on 2016/1/20.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatAdapter";
    private Context mContext;
    private ArrayList<Chat> mChat;


    public ChatAdapter(Context context) {
        mContext = context;
        mChat = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == Chat.SEND)

            v = inflater.inflate(R.layout.item_chat_send, parent, false);
        else
            v = inflater.inflate(R.layout.item_chat_receive, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mChat.get(position).getContent());

        if (!TextUtils.isEmpty(mChat.get(position).getUrl())) {
            holder.mTextViewUrl.setText("查看详情");
            holder.mTextViewUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.browser(mContext, mChat.get(position).getUrl());

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getFlag() == Chat.SEND)
            return Chat.SEND;
        else
            return Chat.RECEIVE;
    }

    /**
     * 加载聊天消息
     *
     * @param text
     */
    public void loadChat(String text) {
        Chat sendText = new Chat(Chat.SEND, text);
        mChat.add(sendText);
        notifyDataSetChanged();
        //识别特定文字
        recognizeText(text);
    }

    /**
     * 识别特定文字
     *
     * @param text
     */
    private void recognizeText(final String text) {
        if (text.contains("放一首歌吧") || text.contains("听歌") || text.contains("播放")) {

            mChat.add(new Chat(Chat.RECEIVE, "即将播放音乐..."));
            notifyDataSetChanged();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (PlayerController.getNowPlaying() != null) {
                        NowPlayingActivity.startIt(PlayerController.getNowPlaying(), (Activity) mContext);
                    } else if (PlayerLib.getSongs().size() != 0) {
                        PlayerController.setQueueAndPosition(PlayerLib.getSongs(), 0);
                        PlayerController.begin();
                        NowPlayingActivity.startIt(PlayerLib.getSongs().get(0), (Activity) mContext);
                    } else {
                        ToastUtils.showToast(mContext.getString(R.string.fab_no_music));
                    }
                }
            }, 1555);

        } else if (text.contains("歌手") || text.contains("歌曲榜") ||
                text.contains("艺人榜") || text.contains("音乐")) {
            mChat.add(new Chat(Chat.RECEIVE, "正在查询" + text + "..."));
            notifyDataSetChanged();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //启动MusicFragment
                    MainActivity.startIt(1, (Activity) mContext);
                }
            }, 1555);

        } else if (text.contains("电话") || text.contains("拨打")) {
            mChat.add(new Chat(Chat.RECEIVE, "正在打开电话..."));
            notifyDataSetChanged();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String number = "";
                    for (int i = 0; i < text.length(); i++) {
                        if (text.charAt(i) >= 48 && text.charAt(i) <= 57) {
                            number += text.charAt(i);
                        }
                    }
                    Utils.dial(mContext, number.length() >= 3 ? number : null);
                }
            }, 1255);
        } else if (text.contains("短信")) {
            mChat.add(new Chat(Chat.RECEIVE, "正在打开短信..."));
            notifyDataSetChanged();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String number = "";
                    for (int i = 0; i < text.length(); i++) {
                        if (text.charAt(i) >= 48 && text.charAt(i) <= 57) {
                            number += text.charAt(i);
                        }
                    }
                    Utils.sms(mContext, number.length() >= 5 ? number : null);
                }
            }, 1255);
        } else if (text.contains("教务")) {

            if (!TextUtils.isEmpty(PreferencesUtils.getString(mContext, mContext.getString(R.string.account)))) {
                mChat.add(new Chat(Chat.RECEIVE, "正在打开..."));
                notifyDataSetChanged();
//                final String username = PreferencesUtils.getString(mContext, mContext.getString(R.string.account));
//                final String password = PreferencesUtils.getString(mContext, mContext.getString(R.string.password));
//                final String md5 = MD5Utils.md5Encode(password);
//                LogUtils.d(URL + " " + username + " " + password + " " + md5);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WebViewActivity.startIt(mContext, Constants.WZ_URL, null);
                    }
                }, 1255);

            } else
                ToastUtils.showToast(mContext.getString(R.string.account_not_bind));
        } else if (text.contains("二维码") || text.contains("条码") || text.contains("扫一扫")) {
            mChat.add(new Chat(Chat.RECEIVE, "正在打开..."));
            notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CaptureActivity.startIt(mContext);
                }
            }, 1255);

        } else
            loadWithTuling(text);
    }

    /**
     * 从图灵接口加载
     *
     * @param text
     */

    private void loadWithTuling(String text) {

        Map<String, String> params = new HashMap<>();
        params.put("key", "3f1a3d85cfbb83fb58ba5c38997343dc");
        params.put("info", text);

        GsonRequest gsonRequest = new GsonRequest<>(Chat.URL,
                Chat.class, params, new Response.Listener<Chat>() {
            @Override
            public void onResponse(Chat response) {
                response.setFlag(Chat.RECEIVE);
                response.setContent(response.getContent().replace(mContext.getString(R.string.tuling), mContext.getString(R.string.umaru)));
                mChat.add(response);
                notifyDataSetChanged();
                if (mContext instanceof ChatActivity) {
                    RecyclerView recyclerView = ((ChatActivity) mContext).getRecyclerView();
//                    recyclerView.smoothScrollToPosition(getItemCount() - 1);
                }
                LogUtils.d(response);
            }
        });

        RequestManager.addRequest(gsonRequest, mContext);
    }

    public void loadRandomWelcomeTexts() {
        String[] welcome_array = mContext.getResources().getStringArray(R.array.welcome_texts);
        int index = (int) (Math.random() * (welcome_array.length));
        Chat welcomeText = new Chat(Chat.RECEIVE, welcome_array[index]);
        mChat.add(welcomeText);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_chat_tv)
        TextView mTextView;
        @Nullable
        @Bind(R.id.item_chat_tv_url)
        TextView mTextViewUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
