package cc.haoduoyu.umaru.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.activities.NowPlayingActivity;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.event.MessageEvent;

/**
 * Created by XP on 2016/1/9.
 */
public class OtherFragment extends BaseFragment {

    //    @Bind(R.id.button1)
//    Button button;
    //不要让客户端去调用默认的构造函数，然后手动地设置fragment的参数。我们直接为它们提供一个静态工厂方法。
    // 这样做比调用默认构造方法好，有两个原因：一个是，它方便别人的调用。另一个是，保证了fragment的构建过程不会出错。
    //通过提供一个静态工厂方法，我们避免了自己犯错--我们再也不用担心不小心忘记初始化fragmnet的参数或者没正确设置参数。
    public static OtherFragment newInstance() {
        OtherFragment fragment = new OtherFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        ButterKnife.bind(this, view);
        initViews();

        return view;
    }

    private void initViews() {

    }

    public void onEvent(MessageEvent event) {

    }

    @OnClick(R.id.button1)
    void startMusic() {
        startActivity(new Intent(getActivity(), NowPlayingActivity.class));
    }

}
