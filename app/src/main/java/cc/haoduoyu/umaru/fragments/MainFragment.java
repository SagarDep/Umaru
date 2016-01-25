package cc.haoduoyu.umaru.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import cc.haoduoyu.umaru.utils.PreferencesUtils;
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
//    @Bind(R.id.welcome_htv2)
//    HTextView hTextView;
    List<City> cities = new ArrayList<>();
    CityDao cityDao;
    City currentCity;

    String ipAddress;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    protected void initViews() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                hTextView.animateText(getString(R.string.welcome2));
//            }
//        }, 1258);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.fragment_main;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cityDao = new CityDao(getContext());
        City city = new City("CN101190302", "danyang");
        cities.add(city);
        city = new City("CN101190401", "suzhou");
        cities.add(city);

        for (City c : cities) {
            cityDao.addCity(c);
        }

        currentCity = cityDao.queryForEq("city_or_county_en", "danyang").get(0);
        loadWeather(currentCity.getCityId());

        loadWeatherPic();

    }

    public void onEvent(MessageEvent event) {
        if (event.message.equals("pic")) {

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
                wCityTv.setText(heWeather.getBasic().getCity());
                wUpdateTimeTv.setText("更新于" + heWeather.getBasic().getUpdate().getLoc().substring(11));
                wNowTxtTv.setText(heWeather.getNow().getCond().getTxt());
                wNowTmpTv.setText(heWeather.getNow().getTmp());

//                Log.d("weather", response.getHeWeather().get(0).getBasic().getCity());
            }
        }));
    }

    /**
     * 得到IP地址
     */
    private void getIp() {
        executeRequest(new StringRequest("http://1.bigggge.sinaapp.com/get_ip.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ipAddress = response.split("<script")[0].replaceAll("\n", "");//用"<script"分割字符串并返回第一个数组元素
//                loadWeather(ipAddress);
                LogUtils.d(ipAddress);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.e(error.getMessage(), error);
            }
        }));

    }


}
