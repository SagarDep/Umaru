package cc.haoduoyu.umaru.ui.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.Response;
import com.apkfuns.logutils.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cc.haoduoyu.umaru.Constants;
import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.base.BaseFragment;
import cc.haoduoyu.umaru.db.dao.CityDao;
import cc.haoduoyu.umaru.event.MessageEvent;
import cc.haoduoyu.umaru.model.City;
import cc.haoduoyu.umaru.model.Weather;
import cc.haoduoyu.umaru.utils.Once;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.SettingUtils;
import cc.haoduoyu.umaru.utils.Utils;
import cc.haoduoyu.umaru.utils.volleyUtils.GsonRequest;

/**
 * Created by XP on 2016/1/9.
 */
public class MainFragment extends BaseFragment implements ViewSwitcher.ViewFactory {

    @Bind(R.id.weather_background)
    ImageView wBackground;
    @Bind(R.id.weather_city)
    TextView wCityTv;//天气城市
    @Bind(R.id.weather_update_time)
    TextView wUpdateTimeTv;//天气时间
    @Bind(R.id.weather_now_txt)
    TextView wNowTxtTv;//天气描述
    @Bind(R.id.weather_now_tmp)
    TextView wNowTmpTv;//天气温度
    @Bind(R.id.weather_suggestion)
    TextView wSugTv;

    CityDao cityDao;
    String currentCityId;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected int provideLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cityDao = new CityDao(getContext());

        new Once(getActivity()).execute("insertToDB", new Once.OnceCallback() {
            @Override
            public void onOnce() {
                insertToDB();
            }
        });
        List<City> cityList = cityDao.queryForEq(City.Q, "danyang");
        LogUtils.d(cityList);
        if (cityList.size() == 0) {
            insertToDB();
        } else {
            currentCityId = cityList.get(0).getCityId();
        }

        if (SettingUtils.getInstance(getActivity()).isEnableCache()
                && !Utils.isNetworkReachable(getActivity())) {
            loadFromPreference();
        } else {
            loadWeather(currentCityId);
        }
        loadWeatherPic();
    }

    private void insertToDB() {
        List<City> cities = new ArrayList<>();

        City city = new City();
        city.setCityId("CN101190302");
        city.setCityEn("danyang");
        cities.add(city);
        city = new City();
        city.setCityId("CN101190401");
        city.setCityEn("suzhou");
        cities.add(city);

        for (City c : cities) {
            cityDao.addCity(c);
        }
        currentCityId = cityDao.queryForEq(City.Q, "danyang").get(0).getCityId();
    }


    public void onEvent(MessageEvent event) {
        if (event.message.equals(MessageEvent.WEATHER_PIC)) {
            loadWeatherPic();
        }

    }

    private void loadWeatherPic() {
        Constants.isDay = PreferencesUtils.getBoolean(getActivity(), "w_pic", false);
        if (Constants.isDay) {
            Glide.with(this).load(Constants.WEATHER_PIC_NIGHT).crossFade().into(wBackground);
        } else {
            Glide.with(this).load(Constants.WEATHER_PIC_DAY).crossFade().into(wBackground);
        }
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

                    wCityTv.setText(heWeather.getBasic().getCity());
                    wNowTxtTv.setText(heWeather.getNow().getCond().getTxt());
                    wNowTmpTv.setText(heWeather.getNow().getTmp());
                    wSugTv.setText(heWeather.getSuggestion().getComf().getTxt());
                    saveToPreference(heWeather);
                }
            }
        }));
    }

    private void saveToPreference(Weather.HeWeather heWeather) {
        PreferencesUtils.setString(getContext(), getActivity().getString(R.string.city), heWeather.getBasic().getCity());
        PreferencesUtils.setString(getContext(), getActivity().getString(R.string.nowtxt), heWeather.getNow().getCond().getTxt());
        PreferencesUtils.setString(getContext(), getActivity().getString(R.string.nowtmp), heWeather.getNow().getTmp());
        PreferencesUtils.setString(getContext(), getActivity().getString(R.string.sug), heWeather.getSuggestion().getComf().getTxt());
    }

    private void loadFromPreference() {
        wCityTv.setText(PreferencesUtils.getString(getActivity(), getActivity().getString(R.string.city)));
        wNowTxtTv.setText(PreferencesUtils.getString(getActivity(), getActivity().getString(R.string.nowtxt)));
        wNowTmpTv.setText(PreferencesUtils.getString(getActivity(), getActivity().getString(R.string.nowtmp)));
        wSugTv.setText(PreferencesUtils.getString(getActivity(), getActivity().getString(R.string.sug)));
    }
}
