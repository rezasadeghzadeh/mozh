package ir.sadeghzadeh.mozhdegani.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;

public class FileChooser {
    private static final String PARENT_DIR = "..";
    private final Activity activity;
    private File currentPath;
    private Dialog dialog;
    private String extension;
    private FileSelectedListener fileListener;
    private ListView list;

    public interface FileSelectedListener {
        void fileSelected(File file);
    }

    class ListOnItemClickListener implements OnItemClickListener {
        ListOnItemClickListener() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int which, long id) {
            File chosenFile = FileChooser.this.getChosenFile((String) FileChooser.this.list.getItemAtPosition(which));
            if (chosenFile.isDirectory()) {
                FileChooser.this.refresh(chosenFile);
                return;
            }
            if (FileChooser.this.fileListener != null) {
                FileChooser.this.fileListener.fileSelected(chosenFile);
            }
            FileChooser.this.dialog.dismiss();
        }
    }

    class DirsFilter implements java.io.FileFilter {
        DirsFilter() {
        }

        public boolean accept(File file) {
            return file.isDirectory() && file.canRead();
        }
    }

    class FileFilter implements java.io.FileFilter {
        FileFilter() {
        }

        public boolean accept(File file) {
            if (file.isHidden() || file.isDirectory() || !file.canRead()) {
                return false;
            }
            if (FileChooser.this.extension == null) {
                return true;
            }
            return file.getName().toLowerCase().endsWith(FileChooser.this.extension);
        }
    }

    class FileListAdapter extends ArrayAdapter {
        FileListAdapter(Context x0, int x1, Object[] x2) {
            super(x0, x1, x2);
        }

        public View getView(int pos, View view, ViewGroup parent) {
            view = super.getView(pos, view, parent);
            ((TextView) view).setSingleLine(true);
            return view;
        }
    }

    public void setExtension(String extension) {
        String str;
        if (extension == null) {
            str = null;
        } else {
            str = extension.toLowerCase();
        }
        this.extension = str;
    }

    public FileChooser setFileListener(FileSelectedListener fileListener) {
        this.fileListener = fileListener;
        return this;
    }

    public FileChooser(Activity activity) {
        this.extension = null;
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.list = new ListView(activity);
        this.list.setOnItemClickListener(new ListOnItemClickListener());
        this.dialog.setContentView(this.list);
        this.dialog.getWindow().setLayout(-1, -1);
        File path = new File("/storage/udisk0");
        if (!path.exists()) {
            path = Environment.getExternalStorageDirectory();
        }
        refresh(path);
    }

    public void showDialog() {
        this.dialog.show();
    }

    private void refresh(File path) {
        int i = 0;
        this.currentPath = path;
        if (path.exists()) {
            String[] fileList;
            int i2;
            File[] dirs = path.listFiles(new DirsFilter());
            File[] files = path.listFiles(new FileFilter());
            int i3 = 0;
            if (path.getParentFile() == null) {
                fileList = new String[(dirs.length + files.length)];
            } else {
                fileList = new String[((dirs.length + files.length) + 1)];
                i2 = 0 + 1;
                fileList[0] = PARENT_DIR;
                i3 = i2;
            }
            Arrays.sort(dirs);
            Arrays.sort(files);
            int length = dirs.length;
            int i4 = 0;
            i2 = i3;
            while (i4 < length) {
                i3 = i2 + 1;
                fileList[i2] = dirs[i4].getName();
                i4++;
                i2 = i3;
            }
            i4 = files.length;
            while (i < i4) {
                i3 = i2 + 1;
                fileList[i2] = files[i].getName();
                i++;
                i2 = i3;
            }
            this.dialog.setTitle(this.currentPath.getPath());
            this.list.setAdapter(new FileListAdapter(this.activity, 17367043, fileList));
        }
    }

    private File getChosenFile(String fileChosen) {
        if (fileChosen.equals(PARENT_DIR)) {
            return this.currentPath.getParentFile();
        }
        return new File(this.currentPath, fileChosen);
    }
}
