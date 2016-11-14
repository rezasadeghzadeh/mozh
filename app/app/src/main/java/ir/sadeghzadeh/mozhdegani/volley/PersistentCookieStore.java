package ir.sadeghzadeh.mozhdegani.volley;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public class PersistentCookieStore implements CookieStore {
    private static final String PREFS_NAME;
    private static final String PREF_DEFAULT_STRING = "";
    private static final String PREF_SESSION_COOKIE = "session_cookie";
    private Context mContext;
    private CookieStore mStore;

    static {
        PREFS_NAME = PersistentCookieStore.class.getName();
    }

    public PersistentCookieStore(Context context) {
        this.mContext = context.getApplicationContext();
        this.mStore = new CookieManager().getCookieStore();
        String jsonSessionCookie = getJsonSessionCookieString();
        if (!jsonSessionCookie.equals(PREF_DEFAULT_STRING)) {
            HttpCookie cookie = (HttpCookie) new Gson().fromJson(jsonSessionCookie, HttpCookie.class);
            this.mStore.add(URI.create(cookie.getDomain()), cookie);
        }
    }

    public void add(URI uri, HttpCookie cookie) {
        if (cookie.getName().equals("sessionid")) {
            remove(URI.create(cookie.getDomain()), cookie);
            saveSessionCookie(cookie);
        }
        this.mStore.add(URI.create(cookie.getDomain()), cookie);
    }

    public List<HttpCookie> get(URI uri) {
        return this.mStore.get(uri);
    }

    public List<HttpCookie> getCookies() {
        return this.mStore.getCookies();
    }

    public List<URI> getURIs() {
        return this.mStore.getURIs();
    }

    public boolean remove(URI uri, HttpCookie cookie) {
        return this.mStore.remove(uri, cookie);
    }

    public boolean removeAll() {
        return this.mStore.removeAll();
    }

    private String getJsonSessionCookieString() {
        return getPrefs().getString(PREF_SESSION_COOKIE, PREF_DEFAULT_STRING);
    }

    private void saveSessionCookie(HttpCookie cookie) {
        String jsonSessionCookieString = new Gson().toJson((Object) cookie);
        Editor editor = getPrefs().edit();
        editor.putString(PREF_SESSION_COOKIE, jsonSessionCookieString);
        editor.apply();
    }

    private SharedPreferences getPrefs() {
        return this.mContext.getSharedPreferences(PREFS_NAME, 0);
    }
}
