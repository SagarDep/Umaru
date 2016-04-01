package cc.haoduoyu.umaru.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.db.DBManager;
import cc.haoduoyu.umaru.event.ContentEvent;
import cc.haoduoyu.umaru.ui.base.ToolbarActivity;
import cc.haoduoyu.umaru.widgets.citypicker.adapter.CityListAdapter;
import cc.haoduoyu.umaru.widgets.citypicker.adapter.ResultListAdapter;
import cc.haoduoyu.umaru.widgets.citypicker.model.City;
import cc.haoduoyu.umaru.widgets.citypicker.model.LocateState;
import cc.haoduoyu.umaru.widgets.citypicker.view.SideLetterBar;
import de.greenrobot.event.EventBus;


/**
 * This code was modified by XP.
 * author zaaach on 2016/1/26.
 */
public class CityPickerActivity extends ToolbarActivity implements View.OnClickListener {

    private ListView mListView;
    private ListView mResultListView;
    private SideLetterBar mLetterBar;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;

    private CityListAdapter mCityAdapter;
    private ResultListAdapter mResultAdapter;
    private List<City> mAllCities = new ArrayList<>();
    private DBManager dbManager;

    private static final int COMPLETED = 0;

//    private AMapLocationClient mLocationClient;

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_city_list;
    }

    public static void startIt(Context context) {
        Intent intent = new Intent(context, CityPickerActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView();
//        initLocation();
//        mCityAdapter.updateLocateState(LocateState.FAILED, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCityAdapter.update();
            }
        }).start();

    }

//    private void initLocation() {
//        mLocationClient = new AMapLocationClient(this);
//        AMapLocationClientOption option = new AMapLocationClientOption();
//        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        option.setOnceLocation(true);
//        mLocationClient.setLocationOption(option);
//        mLocationClient.setLocationListener(new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation aMapLocation) {
//                if (aMapLocation != null) {
//                    if (aMapLocation.getErrorCode() == 0) {
//                        String city = aMapLocation.getCity();
//                        String district = aMapLocation.getDistrict();
//                        Log.e("onLocationChanged", "city: " + city);
//                        Log.e("onLocationChanged", "district: " + district);
//                        String location = StringUtils.extractLocation(city, district);
//                        mCityAdapter.updateLocateState(LocateState.SUCCESS, location);
//                    } else {
//                        //定位失败
//                        mCityAdapter.updateLocateState(LocateState.FAILED, null);
//                    }
//                }
//            }
//        });
//        mLocationClient.startLocation();
//    }

    private void initData() {

        dbManager = new DBManager(CityPickerActivity.this);
        dbManager.copyDBFile();
        mAllCities = dbManager.getAllCities();

        mCityAdapter = new CityListAdapter(CityPickerActivity.this, mAllCities);
        mCityAdapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String name) {
                sendIdToMainFragment(name);
            }

            //如果是定位失败状态
            @Override
            public void onLocateClick() {
                Log.e("onLocateClick", "重新定位...");
                mCityAdapter.updateLocateState(LocateState.LOCATING, "苏州");
//                mLocationClient.startLocation();
            }
        });

        mResultAdapter = new ResultListAdapter(this, null);

    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_all_city);
        mListView.setAdapter(mCityAdapter);
        TextView overlay = (TextView) findViewById(R.id.tv_letter_overlay);
        mLetterBar = (SideLetterBar) findViewById(R.id.side_letter_bar);
        //设置悬浮的TextView
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                LogUtils.d(letter + position);
                mListView.setSelection(position);
            }
        });

        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    clearBtn.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    mResultListView.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    mResultListView.setVisibility(View.VISIBLE);
                    List<City> result = dbManager.searchCity(keyword);
                    if (result == null || result.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        mResultAdapter.changeData(result);
                    }
                }
            }
        });

        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendIdToMainFragment(mResultAdapter.getItem(position).getId());
            }
        });

        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        clearBtn.setOnClickListener(this);
    }

    private void sendIdToMainFragment(String id) {
        finish();
        EventBus.getDefault().post(new ContentEvent(ContentEvent.WEATHER_CITY, id));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_search_clear:
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                mResultListView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mLocationClient.stopLocation();
    }
}
