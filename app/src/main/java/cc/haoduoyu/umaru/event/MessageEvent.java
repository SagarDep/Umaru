package cc.haoduoyu.umaru.event;

/**
 * Created by XP on 2016/1/22.
 */
public class MessageEvent {

    public static final String WEATHER_PIC = "weather_pic";
    public static final String LOAD_DONE = "load_done";
    public static final String SHOW_OR_HIDE_FLOATVIEW = "show_or_hide_float_view";
    public static final String WZ = "wz";

    public final String message;

    public MessageEvent(String message) {
        this.message = message;
    }

}
