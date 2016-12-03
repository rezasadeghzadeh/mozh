package com.loveplusplus.update;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.utils.Util;
import ir.sadeghzadeh.mozhdegani.volley.DownloadFileVolleyRequest;

public class DownloadService extends IntentService {
    // 10-10 19:14:32.618: D/DownloadService(1926): 测试缓存：41234 32kb
    // 10-10 19:16:10.892: D/DownloadService(2069): 测试缓存：41170 1kb
    // 10-10 19:18:21.352: D/DownloadService(2253): 测试缓存：39899 10kb
    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Builder mBuilder;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Builder(this);

        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;

        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        final String urlStr = intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
        InputStream in = null;
        File dir = new File(Environment.getExternalStorageDirectory() + "/download");
        String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
        final File apkFile = new File(dir, apkName);
        try {
            FileOutputStream out = new FileOutputStream(apkFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final DownloadFileVolleyRequest downloadApkRequest = new DownloadFileVolleyRequest(Request.Method.GET, urlStr,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        long received = 0;
                        long lengthOfFile = (long) response.length;
                        if (response != null) {
                            try {
                                BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(apkFile));
                                byte[] data = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                                InputStream input = new ByteArrayInputStream(response);
                                while (true) {
                                    int read = input.read(data);
                                    if (read != -1) {
                                        received += (long) read;
                                        output.write(data, 0, read);
                                        int progress = (int) (received * 100L / lengthOfFile);
                                        updateProgress(progress,received,lengthOfFile);
                                    } else {
                                        output.flush();
                                        output.close();
                                        input.close();
                                        break;
                                    }
                                }
                                installAPk(apkFile);

                                mNotifyManager.cancel(NOTIFICATION_ID);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }, null);
        ApplicationController.getInstance().addToRequestQueue(downloadApkRequest);


/*
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");

            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            //File dir = StorageUtils.getCacheDirectory(this);
            File dir = new File(Environment.getExternalStorageDirectory() + "/download");
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                if (progress != oldProgress) {
                    updateProgress(progress,bytesum,bytetotal);
                }
                oldProgress = progress;
            }

            installAPk(apkFile);

            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
*/
    }

    private void updateProgress(int progress, long downloaded, long total) {
        String msg = String.format("%d%% %d/%d", progress, downloaded, total);
        mBuilder.setContentText(msg).setProgress(100, progress, false);
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void installAPk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}
