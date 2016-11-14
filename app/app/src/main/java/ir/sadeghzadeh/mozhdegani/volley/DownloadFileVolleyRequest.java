package ir.sadeghzadeh.mozhdegani.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdegani.utils.Util;

public class DownloadFileVolleyRequest extends Request<byte[]> {
    private final Listener<byte[]> mListener;
    private Map<String, String> mParams;
    public Map<String, String> responseHeaders;

    public DownloadFileVolleyRequest(int post, String mUrl, Listener<byte[]> listener, ErrorListener errorListener, HashMap<String, String> params) {
        super(post, mUrl, errorListener);
        setShouldCache(false);
        this.mListener = listener;
        this.mParams = params;
        Util.login();
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return this.mParams;
    }

    protected void deliverResponse(byte[] response) {
        this.mListener.onResponse(response);
    }

    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        this.responseHeaders = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return new HashMap();
    }
}
