package cc.haoduoyu.umaru.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.db.CityDao;
import cc.haoduoyu.umaru.db.InsertHelper;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.model.City;
import cc.haoduoyu.umaru.model.Weather;
import cc.haoduoyu.umaru.player.PlayerLib;
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
 * Created by XP on 2016/1/9.
 */
public class MainFragment extends BaseFragment implements ViewSwitcher.ViewFactory {

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

    CityDao cityDao;
    String currentCityId;
    String more;
    List<City> cities;
    String[] citiesStr;
    boolean isSetChart;

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

    }

    @Override
    protected int provideLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDb();

        if (SettingUtils.getInstance(getActivity()).isEnableCache()
                && !Utils.isNetworkReachable(getActivity())) {
            loadFromCache();
        } else {
            loadWeather(currentCityId);
        }
        PlayerLib.scanAll(getActivity());
    }


    private void initDb() {
        cityDao = new CityDao(getContext());

        new Once(getActivity()).execute(getString(R.string.insert_city), new Once.OnceCallback() {
            @Override
            public void onOnce() {
                insertToDB();
            }
        });
        List<City> cityList = cityDao.queryForEq(City.Q, "suzhou");
        LogUtils.d(cityList);
        if (cityList.size() == 0) {
            insertToDB();
        } else {
            currentCityId = cityList.get(0).getCityId();
        }
    }

    private void insertToDB() {
        InsertHelper.insertCity(cityDao);
        currentCityId = cityDao.queryForEq(City.Q, "suzhou").get(0).getCityId();
    }


    public void onEvent(MessageEvent event) {
    }

    @Override
    public View makeView() {
        TextView t = new TextView(getActivity());
        t.setGravity(Gravity.CENTER);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        return t;
    }

    private void loadWeather(String id) {
        LogUtils.d(Weather.URL + id);
        executeRequest(new GsonRequest<>(Weather.URL + id,
                Weather.class, new Response.Listener<Weather>() {
            @Override
            public void onResponse(Weather response) {
                Weather.HeWeather heWeather = response.getHeWeather().get(0);
                if ("ok".equals(heWeather.getStatus())) {

                    showWeather(heWeather);
                    mCache.put(getString(R.string.heweather), heWeather);
                    if (!isSetChart) {
                        ChartUtils.showChart(getActivity(), wChart, heWeather, isSetChart);//展示图表
                        isSetChart = true;
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
    void chooseCity() {
        cities = cityDao.queryAll();
        citiesStr = new String[2];
        for (int i = 0; i < cities.size(); i++) {
            citiesStr[i] = cities.get(i).getCityZh();
        }
        LogUtils.d(cities);
        LogUtils.d(citiesStr);
        final int selectedIndex = PreferencesUtils.getInteger(getActivity(), getString(R.string.s_choice), 0);
        new MaterialDialog.Builder(getActivity())
                .title(R.string.choose_city)
                .items(citiesStr)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        currentCityId = cities.get(which).getCityId();
                        if (selectedIndex != which){
                            isSetChart=false;
                            loadWeather(currentCityId);
                        }
                        PreferencesUtils.setInteger(getActivity(), getString(R.string.s_choice), which);
                        return true;
                    }
                })
                .positiveText(R.string.agree)
                .negativeText(R.string.cancel)
                .show();
    }

}
