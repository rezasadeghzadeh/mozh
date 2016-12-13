package ir.sadeghzadeh.mozhdegani;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdegani.utils.Util;

public class ApplicationController extends Application {
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "session";
    private static final String SET_COOKIE_KEY = "Set-Cookie";
    public static final String TAG = "VolleyPatterns";
    private static ApplicationController sInstance;
    ImageCache imageCache;
    private ImageLoader imageLoader;
    private RequestQueue mRequestQueue;

    public ApplicationController() {
        this.imageCache = new BitmapLruCache();
    }

    public ImageLoader getImageLoaderInstance() {
        if (this.imageLoader == null) {
            this.imageLoader = new ImageLoader(getRequestQueue(), this.imageCache);
        }
        return this.imageLoader;
    }

    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static synchronized ApplicationController getInstance() {
        ApplicationController applicationController;
        synchronized (ApplicationController.class) {
            applicationController = sInstance;
        }
        return applicationController;
    }

    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        if (tag.isEmpty()) {
            tag = TAG;
        }
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(10000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, int timeout) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(timeout, 3, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (this.mRequestQueue != null) {
            this.mRequestQueue.cancelAll(tag);
        }
    }
}
