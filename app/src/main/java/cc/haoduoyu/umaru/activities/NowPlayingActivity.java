package cc.haoduoyu.umaru.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;

import net.steamcrafted.materialiconlib.MaterialIconView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseActivity;
import cc.haoduoyu.umaru.utils.ToastUtils;

/**
 * Created by XP on 2016/1/9.
 */
public class NowPlayingActivity extends BaseActivity {

    @Bind(R.id.album_art)
    ImageView albumart;
    @Bind(R.id.shuffle)
    ImageView shuffle;
    @Bind(R.id.repeat)
    ImageView repeat;
    @Bind(R.id.previous)
    MaterialIconView previous;
    @Bind(R.id.next)
    MaterialIconView next;
    @Bind(R.id.playpausefloating)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nowplaying);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.previous)
    void doPrevious(){
        ToastUtils.showToast("previous");
    }

    @OnClick(R.id.next)
    void doNext(){
        ToastUtils.showToast("next");
    }
}
