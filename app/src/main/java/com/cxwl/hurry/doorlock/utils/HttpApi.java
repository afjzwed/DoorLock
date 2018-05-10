package com.cxwl.hurry.doorlock.utils;

import android.util.Log;

import com.cxwl.hurry.doorlock.http.API;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.cxwl.hurry.doorlock.config.DeviceConfig.SERVER_URL;

/**
 * Created by cts on 17/12/25.
 */

public class HttpApi {

    private static final String TAG = "xiao_";
    private static boolean DEBUG = true;

    public static void setDEBUG(boolean DEBUG) {
        HttpApi.DEBUG = DEBUG;
    }

    public static void i(String m) {
        if (DEBUG) {
            Log.i(TAG, m);
        }
    }

    public static void i(String t, String m) {
        if (DEBUG) {
            Log.i(t, m);
        }
    }

    public static void e(String m) {
        if (DEBUG) {
            Log.e(TAG, m);
        }
    }

    public static void e(String t, String m) {
        if (DEBUG) {
            Log.e(t, m);
        }
    }


    private static HttpApi api;
    private static OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static HttpApi getInstance() {
        if (api == null) {
            api = new HttpApi();
            client = new OkHttpClient();
        }
        return api;
    }

    public String loadHttpforPost(String u, JSONObject j, String t) throws Exception {
        HttpApi.e(TAG, "请求参数 " + j.toString());
        if (!u.contains("http")) {
            u = API.HTTP_HOST + u;
        }
        try {
            HttpApi.i(TAG, "post-url--------------" + u);
            RequestBody body = RequestBody.create(JSON, j.toString());
            Call call = client.newCall(BuildRequest(body, u, t));
            String s = call.execute().body().string();
            HttpApi.e("返回参数" + s);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public String loadHttpforGet(String u, String t) {
        if (!u.contains("http")) {
            u = API.HTTP_HOST + u;
        }
        try {
            HttpApi.i(TAG, "get-url--------------" + u);
            Call call = client.newCall(BuildRequest(null, u, t));
            String s = call.execute().body().string();
            HttpApi.e("返回参数" + s);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public String loadHttpforGet(String u, Map<String, String> params, String t) {
        if (!u.contains("http")) {
            u = API.HTTP_HOST + u;
        }
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            u += sb.toString();
        }
        try {
            HttpApi.i(TAG, "get-url--------------" + u);
            Call call = client.newCall(BuildRequest(null, u, t));
            String s = call.execute().body().string();
            HttpApi.e("返回参数" + s);
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public Calendar loadTime() {
        try {
            URL url = new URL("http://www.baidu.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            long ld = conn.getDate();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(ld);
            return c;
        } catch (Exception e) {
            return null;
        }
    }

    private Request BuildRequest(RequestBody body, String url, String token) {
        Request.Builder builder = new Request.Builder().url(url).header("Accept", "application/json").header
                ("Content-Type", "application/json");
        if (token != null && token.length() > 0) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (body != null) {
            builder.post(body);
        }
        return builder.build();
    }


}
