package cc.haoduoyu.umaru.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.event.ContentEvent;
import cc.haoduoyu.umaru.model.Weather;
import cc.haoduoyu.umaru.player.PlayerLib;
import cc.haoduoyu.umaru.ui.activities.CityPickerActivity;
import cc.haoduoyu.umaru.ui.base.BaseFragment;
import cc.haoduoyu.umaru.utils.Once;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.utils.ui.ChartUtils;
import cc.haoduoyu.umaru.utils.ui.DialogUtils;
import cc.haoduoyu.umaru.utils.ui.SnackbarUtils;
import cc.haoduoyu.umaru.utils.volley.GsonRequest;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * 主Fragment，天气信息
 * Created by XP on 2016/1/9.
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.root_main)
    RelativeLayout root;
    @Bind(R.id.weather_background)
    ImageView wBackground;
    @Bind(R.id.weather_city)
    TextView wCityTv;//天气城市
    @Bind(R.id.weather_now_txt)
    TextView wNowTxtTv;//天气描述
    @Bind(R.id.weather_now_tmp)
    TextView wNowTmpTv;//天气温度
    @Bind(R.id.weather_suggestion)
    TextView wSugTv;
    @Bind(R.id.weather_chart)
    LineChartView wChart;

    String currentCityId;
    String more;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    protected void initViews() {
        DialogUtils.showChatGuide(getActivity());
        if (PreferencesUtils.getBoolean(getActivity(), getString(R.string.night_yes), false)) {
            root.setBackgroundColor(getResources().getColor(R.color.md_grey_800));
            Glide.with(this).load(Constants.WEATHER_PIC_NIGHT).crossFade().into(wBackground);
        } else {
            Glide.with(this).load(Constants.WEATHER_PIC_DAY).crossFade().into(wBackground);
        }

        new Once(getActivity()).execute(getString(R.string.once), new Once.OnceCallback() {
            @Override
            public void onOnce() {
                CityPickerActivity.startIt(getActivity());
            }
        });

    }

    @Override
    protected int provideLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentCityId = PreferencesUtils.getString(getActivity(), getString(R.string.city_id));
        if (SettingUtils.getInstance(getActivity()).isEnableCache()
                && !Utils.isNetworkReachable(getActivity())) {
            loadFromCache();
            LogUtils.d("loadFromCache");
        } else {
            loadWeather(!TextUtils.isEmpty(currentCityId) ? currentCityId : "CN101010100", true);
        }
        PlayerLib.scanAll(getActivity());
    }

    public void onEvent(ContentEvent event) {
        if (event.type.equals(ContentEvent.WEATHER_CITY)) {
            loadWeather(event.message, true);
            currentCityId = event.message;
            PreferencesUtils.setString(getActivity(), getString(R.string.city_id), event.message);
        }
    }

    private void loadWeather(String id, final boolean refreshChart) {
        LogUtils.d(Weather.URL + id);
        executeRequest(new GsonRequest<>(Weather.URL + id,
                Weather.class, new Response.Listener<Weather>() {
            @Override
            public void onResponse(Weather response) {
                Weather.HeWeather heWeather = response.getHeWeather().get(0);
                if ("ok".equals(heWeather.getStatus())) {

                    showWeather(heWeather);
                    mCache.put(getString(R.string.heweather), heWeather);
                    if (refreshChart) {
                        ChartUtils.showChart(getActivity(), wChart, heWeather, refreshChart);//展示图表
                    }
                }
            }
        }));
    }

    private void showWeather(Weather.HeWeather heWeather) {
        wCityTv.setText(heWeather.getBasic().getCity());
        wNowTxtTv.setText(heWeather.getNow().getCond().getTxt());
        wNowTmpTv.setText(heWeather.getNow().getTmp());
        if (heWeather.getSuggestion() != null) {//接口改了
            wSugTv.setText(heWeather.getSuggestion().getComf().getTxt());
            more = heWeather.getSuggestion().getComf().getTxt();
        }
    }


    private void loadFromCache() {
        Weather.HeWeather heWeather = (Weather.HeWeather) mCache.getAsObject(getString(R.string.heweather));
        if (heWeather != null)
            showWeather(heWeather);
    }

    @OnClick(R.id.weather_more)
    void more() {
        if (TextUtils.isEmpty(more)) {
            more = getString(R.string.net_error);
        }
        SnackbarUtils.show(wBackground, more, 2888);
    }

    @OnClick(R.id.weather_city)
    void chooseCity1() {
        CityPickerActivity.startIt(getActivity());
    }

    @OnClick(R.id.weather_now_tmp)
    void chooseCity2() {
        CityPickerActivity.startIt(getActivity());
    }
}
