package cc.haoduoyu.umaru.widgets.citypicker.model;

/**
 * author zaaach on 2016/1/26.
 */
public class City {
    private String name;
    private String pinyin;
    private String id;

    public City() {
    }

    public City(String name, String pinyin, String id) {
        this.name = name;
        this.pinyin = pinyin;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
