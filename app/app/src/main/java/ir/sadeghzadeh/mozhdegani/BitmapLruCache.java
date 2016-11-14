package ir.sadeghzadeh.mozhdegani;

import android.graphics.Bitmap;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.util.LruCache;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {
    public BitmapLruCache() {
        this(getDefaultLruCacheSize());
    }

    public BitmapLruCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    protected int sizeOf(String key, Bitmap value) {
        return (value.getRowBytes() * value.getHeight()) / AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT;
    }

    public Bitmap getBitmap(String url) {
        return (Bitmap) get(url);
    }

    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    public static int getDefaultLruCacheSize() {
        return ((int) (Runtime.getRuntime().maxMemory() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID)) / 8;
    }
}
