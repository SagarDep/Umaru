package cc.haoduoyu.umaru.event;

/**
 * Created by XP on 2016/3/4.
 */
public class ContentEvent {

    public static final String WEATHER_CITY = "weather_city";

    public final String type;
    public final String message;

    public ContentEvent(String type, String message) {
        this.type = type;
        this.message = message;
    }
}
