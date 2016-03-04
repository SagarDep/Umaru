package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.Bind;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.ui.adapter.ChatAdapter;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.widgets.RevealBackgroundView;
import cc.haoduoyu.umaru.widgets.SendButton;

/**
 * Created by XP on 2016/1/12.
 */
public class ChatActivity extends ToolbarActivity implements SendButton.OnSendClickListener, RevealBackgroundView.OnStateChangeListener {

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @Bind(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @Bind(R.id.vChatRoot)
    RelativeLayout vChatRoot;
    @Bind(R.id.llSend)
    LinearLayout sendLl;
    @Bind(R.id.etSend)
    EditText sendEt;
    @Bind(R.id.btnSend)
    SendButton sendBtn;
    @Bind(R.id.chatRecyclerview)
    RecyclerView mRecyclerview;
    ChatAdapter mAdapter;


    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_chat;
    }

    public static void startIt(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, ChatActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);//启动动画
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        //动画开关
        if (SettingUtils.getInstance(this).isEnableAnimations() && getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION) != null)
            setupRevealBackground(savedInstanceState);
        else vRevealBackground.setToFinishedFrame();

    }

    private void initViews() {
        mAppBar.setBackgroundColor(Color.TRANSPARENT);
        mAdapter = new ChatAdapter(this);
        mAdapter.loadRandomWelcomeTexts();
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setAdapter(mAdapter);

        //当一个视图树将要绘制时调用这个回调函数
        sendLl.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                sendLl.getViewTreeObserver().removeOnPreDrawListener(this);//去掉这句不会出现view
                sendLl.setTranslationY(sendLl.getHeight());
                return true;
            }
        });
//        sendLl.setTranslationY(sendLl.getHeight());//写在这里没有动画
        vRevealBackground.setOnStateChangeListener(this);
        sendBtn.setOnSendClickListener(this);
    }


    /**
     * 启动动画
     *
     * @param savedInstanceState
     */
    private void setupRevealBackground(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
//            commentsAdapter.addItem();
//            commentsAdapter.setAnimationsLocked(false);
//            commentsAdapter.setDelayEnterAnimation(false);
//            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

//            sendEt.setText(null);
            hideKeyboard();
            mAdapter.loadChat(sendEt.getText().toString().replace("\n", "").replace(" ", ""));
            mRecyclerview.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            sendBtn.setCurrentState(SendButton.STATE_DONE);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(sendEt.getText())) {
            sendBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));//translate画面转换位置移动动画效果

            return false;
        }

        return true;
    }

    /**
     * RevealBackgroundView
     *
     * @param state
     */
    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vChatRoot.setVisibility(View.VISIBLE);
            sendLl.animate().translationY(0).setDuration(400)
                    .setInterpolator(new DecelerateInterpolator());
        } else {
            vChatRoot.setVisibility(View.INVISIBLE);

        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerview;
    }

    private void hideKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onEvent(MessageEvent event) {
        if (event.message.equals(MessageEvent.WEATHER_PIC)) {
//            loadWeatherPic();
        }

    }
}
