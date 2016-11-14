package ir.sadeghzadeh.mozhdegani.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdegani.utils.Util;

public class CustomMultipartVolleyRequest extends Request<String> {
    MultipartEntity entity;
    private long fileLength;
    private ErrorListener mEListener;
    private Listener<String> mListener;
    private MultipartProgressListener multipartProgressListener;

    public static class CountingOutputStream extends FilterOutputStream {
        private long fileLength;
        private final MultipartProgressListener progListener;
        private long transferred;

        public CountingOutputStream(OutputStream out, long fileLength, MultipartProgressListener listener) {
            super(out);
            this.fileLength = fileLength;
            this.progListener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
            if (this.progListener != null) {
                this.transferred += (long) len;
                int prog = (int) ((this.transferred * 100) / this.fileLength);
                Log.d("Upload", "Line 175:" + prog);
                if (prog > 100) {
                    prog = 100;
                }
                this.progListener.transferred(this.transferred, prog);
            }
        }

        public void write(int b) throws IOException {
            this.out.write(b);
            if (this.progListener != null) {
                this.transferred++;
                int prog = (int) ((this.transferred * 100) / this.fileLength);
                Log.d("Upload", "Line 185:" + prog);
                if (prog > 100) {
                    prog = 100;
                }
                this.progListener.transferred(this.transferred, prog);
            }
        }
    }

    public interface MultipartProgressListener {
        void transferred(long j, int i);
    }

    public CustomMultipartVolleyRequest(String url, ErrorListener eListener, Listener<String> rListener, MultipartEntity entity, Long fileLength, MultipartProgressListener multipartProgressListener) throws AuthFailureError {
        super(Method.POST, url, eListener);
        this.mListener = null;
        this.fileLength = 0;
        this.mListener = rListener;
        this.mEListener = eListener;
        this.entity = entity;
        this.fileLength = fileLength.longValue();
        this.multipartProgressListener = multipartProgressListener;
        //Util.login();
    }

    public String getBodyContentType() {
        return this.entity.getContentType().getValue();
    }

    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            this.entity.writeTo(new CountingOutputStream(bos, this.fileLength, this.multipartProgressListener));
        } catch (IOException e) {
            Log.d("Multipart Volley Req:","IOException writing to ByteArrayOutputStream : " + e.getMessage().toString());
        }
        return bos.toByteArray();
    }

    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new String(response.data, HTTP.UTF_8), getCacheEntry());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.success(new String(response.data), getCacheEntry());
        }
    }

    protected void deliverResponse(String response) {
        this.mListener.onResponse(response);
    }

    public void deliverError(VolleyError error) {
        super.deliverError(error);
    }

    public Map<String, String> getHeaders() throws AuthFailureError {
        return new HashMap();
    }

    @Override
    public Priority getPriority() {
        Request.Priority mPriority = Priority.HIGH;
        return mPriority;
    }

}
