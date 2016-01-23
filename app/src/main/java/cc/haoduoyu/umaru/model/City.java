package cc.haoduoyu.umaru.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * City实体类
 * Created by XP on 2016/1/19.
 */

@DatabaseTable(tableName = "city")
public class City {
    @DatabaseField(columnName = "city_id", id = true)//唯一
    private String cityId;
    @DatabaseField(columnName = "city_or_county_en")
    private String cityEn;
    @DatabaseField(columnName = "city_or_county_zh")
    private String cityZh;
    @DatabaseField(columnName = "area")
    private String cityArea;
    @DatabaseField(columnName = "province_or_city")
    private String province;

    public City() {
        // ORMLite needs a no-arg constructor
    }

    public City(String cityId, String cityEn) {
        this.cityId = cityId;
        this.cityEn = cityEn;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityEn() {
        return cityEn;
    }

    public void setCityEn(String cityEn) {
        this.cityEn = cityEn;
    }

    public String getCityZh() {
        return cityZh;
    }

    public void setCityZh(String cityZh) {
        this.cityZh = cityZh;
    }

    public String getCityArea() {
        return cityArea;
    }

    public void setCityArea(String cityArea) {
        this.cityArea = cityArea;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}



