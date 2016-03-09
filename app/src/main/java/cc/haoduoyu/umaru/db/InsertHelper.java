package cc.haoduoyu.umaru.db;

import java.util.ArrayList;
import java.util.List;

import cc.haoduoyu.umaru.model.City;

/**
 * Created by XP on 2016/3/8.
 */
public class InsertHelper {

    public static void insertCity(CityDao cityDao) {
        List<City> cities = new ArrayList<>();

        City city = new City();
        city.setCityZh("丹阳");
        city.setCityId("CN101190302");
        city.setCityEn("danyang");
        cities.add(city);
        city = new City();
        city.setCityZh("苏州");
        city.setCityId("CN101190401");
        city.setCityEn("suzhou");
        cities.add(city);

        for (City c : cities) {
            cityDao.addCity(c);
        }
    }
}
