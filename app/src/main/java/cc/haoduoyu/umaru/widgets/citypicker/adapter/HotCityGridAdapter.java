package cc.haoduoyu.umaru.widgets.citypicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cc.haoduoyu.umaru.R;
import cc.haoduoyu.umaru.widgets.citypicker.model.City;

/**
 * author zaaach on 2016/1/26.
 */
public class HotCityGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<City> mCities;

    public HotCityGridAdapter(Context context) {
        this.mContext = context;
        mCities = new ArrayList<>();
        mCities.add(new City("北京","","CN101010100"));
        mCities.add(new City("上海","","CN101020100"));
        mCities.add(new City("广州","","CN101280101"));
        mCities.add(new City("深圳","","CN101280601"));
        mCities.add(new City("杭州","","CN101210101"));
        mCities.add(new City("南京","","CN101190101"));
        mCities.add(new City("苏州","","CN101190401"));
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public City getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HotCityViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_city_gridview, parent, false);
            holder = new HotCityViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tv_hot_city_name);
            view.setTag(holder);
        }else{
            holder = (HotCityViewHolder) view.getTag();
        }
        holder.name.setText(mCities.get(position).getName());
        return view;
    }

    public static class HotCityViewHolder{
        TextView name;
    }
}
