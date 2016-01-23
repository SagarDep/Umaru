package cc.haoduoyu.umaru.db.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.haoduoyu.umaru.db.DBHelper;
import cc.haoduoyu.umaru.model.City;

/**
 * ChatDao
 * Created by XP on 2016/1/19.
 */
public class CityDao {
    //对每个Bean创建一个XXXDao来处理当前Bean的数据库操作，
    // 真正去和数据库打交道的对象，通过getDao（T t）进行获取
    private Context mContext;
    private Dao<City, Integer> cityDao;
    private DBHelper helper;

    public CityDao(Context context) {
        mContext = context;
        try {
            helper = DBHelper.getHelper(context);
            cityDao = helper.getDao(City.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个城市信息
     *
     * @param city
     */
    public void addCity(City city) {
        try {
            cityDao.create(city);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加一个城市信息
     *
     * @param city
     */
    public void addCityIfNotExists(City city, Map<String, Object> where) {
        try {
            if (cityDao.queryForFieldValues(where).size() < 1) {
                cityDao.createIfNotExists(city);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询一条记录
     *
     * @param filedName
     * @param value
     * @return
     */
    public List<City> queryForEq(String filedName, Object value) {
        try {
            return cityDao.queryForEq(filedName, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    /**
     * 查询所有记录
     */
    private List<City> queryAll() {
        List<City> cities = null;
        try {
            cities = cityDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    /**
     * 删除全部记录
     */
    public void deleteAll() {
        try {
            cityDao.delete(queryAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
