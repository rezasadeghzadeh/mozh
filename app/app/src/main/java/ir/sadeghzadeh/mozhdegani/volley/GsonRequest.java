package ir.sadeghzadeh.mozhdegani.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.utils.Util;

public class GsonRequest<T> extends Request<T> {
    private final Class<T> clazz;
    private final Gson gson;
    private final Map<String, String> headers;
    private final Listener<T> listener;
    private final Map<String, String> params;
    String url;
    public GsonRequest(String url, Class<T> clazz, Map<String, String> params, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.url = url;
        this.gson = new Gson();
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.params = params;
        Util.login();
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }


    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap();
        if (this.headers != null) {
            headers.putAll(this.headers);
        } else {
            headers.putAll(super.getHeaders());
        }
        String token = Util.fetchFromPreferences(Const.TOKEN);
        if(token != null && !token.isEmpty()){
            headers.put(Const.AUTHORIZATION, Const.BEARER + token);
        }
        return headers;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }

    @Override
    public String getUrl() {
        if(params == null){
            return url;
        }
        StringBuilder stringBuilder = new StringBuilder(url);
        int i = 1;
        for (Map.Entry<String,String> entry: params.entrySet()) {
            String key;
            String value;
            try {
                key = URLEncoder.encode(entry.getKey(), "UTF-8");
                value = URLEncoder.encode(entry.getValue(), "UTF-8");
                if(i == 1) {
                    stringBuilder.append("?" + key + "=" + value);
                } else {
                    stringBuilder.append("&" + key + "=" + value);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;

        }
        String url = stringBuilder.toString();

        return url;
    }

}
