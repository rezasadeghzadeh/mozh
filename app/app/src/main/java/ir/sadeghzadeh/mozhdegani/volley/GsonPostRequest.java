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
import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdegani.entity.Item;
import ir.sadeghzadeh.mozhdegani.utils.Util;

public class GsonPostRequest<T> extends Request<T> {
    private final Class<T> clazz;
    private final Gson gson;
    private final Map<String, String> headers;
    private final Listener<T> listener;

    public GsonPostRequest(String url, Class<T> clazz, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.gson = new Gson();
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        Util.login();
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

    protected void deliverResponse(T response) {
        this.listener.onResponse(response);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap();
        if (this.headers != null) {
            headers.putAll(this.headers);
        } else {
            headers.putAll(super.getHeaders());
        }
        return headers;
    }
}
