package cc.haoduoyu.umaru.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Chat实体类
 * 图灵机器人 http://www.tuling123.com/
 * Created by XP on 2016/1/20.
 */
@DatabaseTable(tableName = "city")
public class Chat implements Serializable {

    public static final String URL = "http://www.tuling123.com/openapi/api";
    public static final int SEND = 1;
    public static final int RECEIVE = 2;

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "flag")
    private int flag;
    @DatabaseField(columnName = "code")
    private int code;
    @SerializedName("text")
    @DatabaseField(columnName = "content")
    private String content;
    @DatabaseField(columnName = "time")
    private String time;
    @DatabaseField(columnName = "url")
    private String url;

    public Chat(int flag, String content) {
        this.flag = flag;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
