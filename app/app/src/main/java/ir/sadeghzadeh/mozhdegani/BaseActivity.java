package ir.sadeghzadeh.mozhdegani;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by reza on 11/2/16.
 */
public class BaseActivity extends AppCompatActivity {
    public void addFragmentToContainer(Fragment fragment, String tag) {
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Log.e("addFragmentToContainer", e.toString());
        }
    }
}
