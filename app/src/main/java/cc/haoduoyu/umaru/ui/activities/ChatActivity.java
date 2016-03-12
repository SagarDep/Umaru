package cc.haoduoyu.umaru.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.apkfuns.logutils.LogUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.ui.adapter.ChatAdapter;
import cc.haoduoyu.umaru.utils.JsonParser;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.widgets.GLayoutManager;
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
    RecognizerDialog iatDialog;
    ChatAdapter mAdapter;

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_chat;
    }

    public static void startIt(int[] startingLocation, Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);//启动动画
        context.startActivity(intent);
    }

    public static void startIt(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initXf();

        //动画开关
        if (SettingUtils.getInstance(this).isEnableAnimations() && getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION) != null)
            setupRevealBackground(savedInstanceState);
        else vRevealBackground.setToFinishedFrame();

    }

    private void initViews() {
        mAppBar.setBackgroundColor(Color.TRANSPARENT);
        mAdapter = new ChatAdapter(this);
        mAdapter.loadRandomWelcomeTexts();
        mRecyclerview.setLayoutManager(new GLayoutManager(this));
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
            loadChat();
        }
    }

    private void loadChat() {
        hideKeyboard();
        mAdapter.loadChat(sendEt.getText().toString().replace("\n", "").replace(" ", ""));
        mRecyclerview.smoothScrollToPosition(mAdapter.getItemCount() - 1);
        sendBtn.setCurrentState(SendButton.STATE_DONE);
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(sendEt.getText())) {
            sendBtn.startAnimation(AnimationUtils
                    .loadAnimation(this, R.anim.shake_error));//translate画面转换位置移动动画效果
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

    private void initXf() {
        //创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        iatDialog = new RecognizerDialog(this, mInitListener);
        //设置不带标点
        iatDialog.setParameter(SpeechConstant.ASR_PTT, "0");
        iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        //普通话：mandarin(默认)粤 语：cantonese四川话：lmz河南话：henanese
        iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
        //应用领域用于听写和语音语义服务。当前支持的应用领域有：短信和日常用语：iat (默认)视频：video地图：poi音乐：music
        iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
    }

    @OnClick(R.id.mic)
    void mic() {
        //设置回调接口
        iatDialog.setListener(mRecognizerDialogListener);
        //开始听写
        iatDialog.show();
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                LogUtils.d(getString(R.string.error) + code);

            }
        }
    };

    /**
     * 讯飞听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {

        public void onResult(RecognizerResult results, boolean isLast) {
            if (!isLast)
                parseResults(results);
        }

        public void onError(SpeechError error) {
            LogUtils.d(error.getPlainDescription(true));
        }

    };

    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    private void parseResults(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        LogUtils.json(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段,第几句
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        sendEt.setText(resultBuffer.toString());
        loadChat();
        LogUtils.d(resultBuffer.toString());
        LogUtils.d(mIatResults);
    }

}
