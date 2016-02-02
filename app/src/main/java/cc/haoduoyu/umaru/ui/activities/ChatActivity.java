package cc.haoduoyu.umaru.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import cc.haoduoyu.umaru.BDConstants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.ToolbarActivity;
import cc.haoduoyu.umaru.ui.adapter.ChatAdapter;
import cc.haoduoyu.umaru.widgets.RevealBackgroundView;
import cc.haoduoyu.umaru.widgets.SendButton;

/**
 * Created by XP on 2016/1/12.
 */
public class ChatActivity extends ToolbarActivity implements RecognitionListener, SendButton.OnSendClickListener, RevealBackgroundView.OnStateChangeListener {

    private static final String TAG = "ChatActivity";
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final int EVENT_ERROR = 11;
    private TextView txtResult;
    private TextView txtLog;
    private Button btn;
    private Button setting;

    View speechTips;

    View speechWave;
    private SpeechRecognizer speechRecognizer;
    private long speechEndTime = -1;

    @Bind(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @Bind(R.id.vChatRoot)
    LinearLayout vChatRoot;
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
//        initViews();

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

        setupRevealBackground(savedInstanceState);

        sendBtn.setOnSendClickListener(this);
    }


    /**
     * 启动动画
     *
     * @param savedInstanceState
     */
    private void setupRevealBackground(Bundle savedInstanceState) {
//        vRevealBackground.setFillPaintColor(Color.GRAY);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null || savedInstanceState != null) {
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

//    private void initViews() {
//        txtResult = (TextView) findViewById(R.id.txtResult);
//        txtLog = (TextView) findViewById(R.id.txtLog);
//        btn = (Button) findViewById(R.id.btn);
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
//        speechRecognizer.setRecognitionListener(this);
//
//        speechTips = View.inflate(this, R.layout.bd_asr_popup_speech, null);//音频指示器
//        speechWave = speechTips.findViewById(R.id.wave);
//        speechTips.setVisibility(View.GONE);
//        addContentView(speechTips,
//                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));//添加View
//        btn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        speechTips.setVisibility(View.VISIBLE);
//                        speechRecognizer.cancel();
//                        Intent intent = new Intent();
//                        bindParams(intent);
//                        intent.putExtra("vad", "touch");
//                        txtResult.setText("");
//                        txtLog.setText("");
//                        speechRecognizer.startListening(intent);
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        speechRecognizer.stopListening();
//                        speechTips.setVisibility(View.GONE);
//                        break;
//                }
//                return false;
//            }
//        });
//
//    }


    public void bindParams(Intent intent) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("tips_sound", true)) {//可通过SP设置是否打开提示音
            intent.putExtra(BDConstants.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(BDConstants.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(BDConstants.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(BDConstants.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(BDConstants.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(BDConstants.EXTRA_INFILE)) {//音频源
            String tmp = sp.getString(BDConstants.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(BDConstants.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(BDConstants.EXTRA_OUTFILE, false)) {//保存识别过程产生的录音文件
            intent.putExtra(BDConstants.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.contains(BDConstants.EXTRA_SAMPLE)) {//采样率
            String tmp = sp.getString(BDConstants.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BDConstants.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(BDConstants.EXTRA_LANGUAGE)) {//语种
            String tmp = sp.getString(BDConstants.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BDConstants.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(BDConstants.EXTRA_NLU)) {//语义解析设置
            String tmp = sp.getString(BDConstants.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BDConstants.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(BDConstants.EXTRA_VAD)) {//语音活动检测
            String tmp = sp.getString(BDConstants.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BDConstants.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(BDConstants.EXTRA_PROP)) {//垂直领域
            String tmp = sp.getString(BDConstants.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(BDConstants.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }
        // offline asr
        {
            intent.putExtra(BDConstants.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            intent.putExtra(BDConstants.EXTRA_LICENSE_FILE_PATH, "/sdcard/easr/license-tmp-20150530.txt");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(BDConstants.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(BDConstants.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(BDConstants.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        return slotData.toString();
    }

    //只有当此方法回调之后才能开始说话，否则会影响识别效果
    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    //当用户开始说话后，将会回调此方法
    @Override
    public void onBeginningOfSpeech() {

    }

    //引擎将对每一帧语音进行回调一次改方法
    @Override
    public void onRmsChanged(float rmsdB) {
        final int VTAG = 0xFF00AA01;
        Integer rawHeight = (Integer) speechWave.getTag(VTAG);
        if (rawHeight == null) {
            rawHeight = speechWave.getLayoutParams().height;
            speechWave.setTag(VTAG, rawHeight);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) speechWave.getLayoutParams();
        params.height = (int) (rawHeight * rmsdB * 0.01);
        params.height = Math.max(params.height, speechWave.getMeasuredWidth());
        speechWave.setLayoutParams(params);
    }

    //此方法会被回调多次，buffer是当前帧对应的PCM语音数据，拼接后可得到完整的录音数据
    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    //当用户停止说话后，将会回调此方法
    @Override
    public void onEndOfSpeech() {

    }

    //识别出错
    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        print("识别失败：" + sb.toString());
    }

    //识别结果
    @Override
    public void onResults(Bundle results) {
        long end2finish = System.currentTimeMillis() - speechEndTime;
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        print("识别成功：" + Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String json_res = results.getString("origin_result");
        try {
            print("origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            print("origin_result=[warning: bad json]\n" + json_res);
        }
        btn.setText("开始");
        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        txtResult.setText(nbest.get(0) + strEnd2Finish);
    }

    //临时结果
    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> nbest = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest.size() > 0) {
            print("~临时识别结果：" + Arrays.toString(nbest.toArray(new String[0])));
            txtResult.setText(nbest.get(0));
        }
    }

    //识别事件
    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                print("EVENT_ERROR, " + reason);
                break;
        }
    }

    private void print(String msg) {
        txtLog.append(msg + "\n");
        ScrollView sv = (ScrollView) txtLog.getParent();
        sv.smoothScrollTo(0, 1000000);
        Log.d(TAG, "----" + msg);
    }


    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
//            commentsAdapter.addItem();
//            commentsAdapter.setAnimationsLocked(false);
//            commentsAdapter.setDelayEnterAnimation(false);
//            rvComments.smoothScrollBy(0, rvComments.getChildAt(0).getHeight() * commentsAdapter.getItemCount());

//            sendEt.setText(null);
            mAdapter.loadChat(sendEt.getText().toString().replace("\n", "").replace(" ", ""));
            mRecyclerview.smoothScrollToPosition(mAdapter.getItemCount() - 1);
//            mAdapter.notifyDataSetChanged();
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
}
