package cc.haoduoyu.umaru.utils.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Gson解析器
 * Created by XP on 2016/1/10.
 */
public class GsonRequest<T> extends Request<T> {

    public static final String TAG = "GsonRequest";

    private final Response.Listener<T> mListener;

    private Gson mGson;

    private Class<T> mClass;

    private Map<String, String> mParams;

    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mClass = clazz;
        mListener = listener;
    }

    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> params, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {

        super(method, url, errorListener);
        mGson = new Gson();
        mClass = clazz;
        mParams = params;
        mListener = listener;
    }

    /**
     * GET
     *
     * @param url
     * @param clazz
     * @param listener
     */
    public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener) {
        this(Method.GET, url, clazz, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.e(error);
            }
        });
        LogUtils.d("url-get " + url);
    }

    /**
     * POST
     *
     * @param url
     * @param clazz
     * @param params
     * @param listener
     */
    public GsonRequest(String url, Class<T> clazz, Map<String, String> params, Response.Listener<T> listener) {
        this(Method.POST, url, clazz, params, listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.e(error.getMessage());
            }
        });
        LogUtils.d("url-post " + "params: " + params);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(jsonString, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            LogUtils.d(TAG + "-error", e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

}
