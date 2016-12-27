package ir.sadeghzadeh.mozhdeh.utils;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {
    private Context context;
    private UncaughtExceptionHandler defaultHandler;

    public ExceptionHandler(Context context) {
        this.context = context;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        try{
            File file = new File(Util.getDownloadDirectoryPath() + "/stacktrace.txt");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream os = new FileOutputStream(file);
            os.write(stacktrace.getBytes());
            os.flush();
            os.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        this.defaultHandler.uncaughtException(thread, ex);
    }
}
