package cc.haoduoyu.umaru.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import cc.haoduoyu.umaru.R;


/**
 * Created by froger_mcs on 01.12.14.
 */
public class SendButton extends ViewAnimator implements View.OnClickListener {
    public static final int STATE_SEND = 0;
    public static final int STATE_DONE = 1;

    private static final long RESET_STATE_DELAY_MILLIS = 2000;

    private int currentState;

    private OnSendClickListener onSendClickListener;

    private Runnable revertStateRunnable = new Runnable() {
        @Override
        public void run() {
            setCurrentState(STATE_SEND);
        }
    };

    public SendButton(Context context) {
        super(context);
        init();
    }

    public SendButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_send_button, this, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        currentState = STATE_SEND;
        super.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(revertStateRunnable);
        super.onDetachedFromWindow();
    }

    public void setCurrentState(int state) {
        if (state == currentState) {
            return;
        }

        currentState = state;
        if (state == STATE_DONE) {
//            setEnabled(false);
            postDelayed(revertStateRunnable, RESET_STATE_DELAY_MILLIS);//延迟2000执行SEND动画
            setInAnimation(getContext(), R.anim.slide_in_done);  //展示位移动画
            setOutAnimation(getContext(), R.anim.slide_out_send);
        } else if (state == STATE_SEND) {
//            setEnabled(true);
            setInAnimation(getContext(), R.anim.slide_in_send);
            setOutAnimation(getContext(), R.anim.slide_out_done);
        }
        showNext();//通过showNext手动切换文字 Manually shows the next child.
    }

    @Override
    public void onClick(View v) {
        if (onSendClickListener != null) {
            onSendClickListener.onSendClickListener(this);
        }
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        //Do nothing, you have you own onClickListener implementation (OnSendClickListener)
    }

    public interface OnSendClickListener {
        void onSendClickListener(View v);
    }


    //接口用于你在某个情景下执行相应的操作，此处在onClick方法里。
    // 类里面声明接口，之后在这个类里面执行在某个情景下需要执行的方法，
    // 而且在这个方法里面为声明的接口对象赋值。最后在其他的类中使用这个功能类就可以了。
    // yhx中，我创建了这样的接口
    // public interface OnCommunicationClickListener {
    //    void onCommentsClick(View v, int position);
    //    void onLikeClick(View v);
    //  }
    // 方便fragment与adapter交互
    //  adapter中的控件执行操作并传值，fragment得到值并执行后续的操作
}