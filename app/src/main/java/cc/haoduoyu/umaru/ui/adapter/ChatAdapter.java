package cc.haoduoyu.umaru.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;
import com.thefinestartist.finestwebview.FinestWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.model.Chat;
import cc.haoduoyu.umaru.ui.activities.ChatActivity;
import cc.haoduoyu.umaru.utils.volleyUtils.GsonRequest;
import cc.haoduoyu.umaru.utils.volleyUtils.RequestManager;

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
            holder.mTextViewUrl.setText("点击查看信息");
            holder.mTextViewUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FinestWebView.Builder((Activity) mContext).show(mChat.get(position).getUrl());
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

    public void loadData() {

        for (int i = 0; i < 100; i++) {
            mChat.add(new Chat(Chat.RECEIVE, i + ""));
        }
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

        // 该方法内部实现了在每个观察者上面调用onChanged事件。
        // 每当发现数据集有改变的情况，或者读取到数据的新状态时，就会调用此方法
        Map<String, String> params = new HashMap<>();
        params.put("key", "3f1a3d85cfbb83fb58ba5c38997343dc");
        params.put("info", text);

        GsonRequest gsonRequest = new GsonRequest<>(Chat.URL,
                Chat.class, params, new Response.Listener<Chat>() {
            @Override
            public void onResponse(Chat response) {
                response.setFlag(Chat.RECEIVE);
                mChat.add(response);
                notifyDataSetChanged();
                if (mContext instanceof ChatActivity) {
                    RecyclerView recyclerView = ((ChatActivity) mContext).getRecyclerView();
                    recyclerView.smoothScrollToPosition(getItemCount() - 1);
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
//public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//    class ViewHolder0 extends RecyclerView.ViewHolder {
//        ...
//    }
//
//    class ViewHolder2 extends RecyclerView.ViewHolder {
//        ...
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        // Just as an example, return 0 or 2 depending on position
//        // Note that unlike in ListView adapters, types don't have to be contiguous
//        return position % 2 * 2;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case 0: return new ViewHolder0(...);
//            case 2: return new ViewHolder2(...);
//            ...
//        }
//    }
//}