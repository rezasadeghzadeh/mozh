package ir.sadeghzadeh.mozhdegani;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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

    public boolean isMarshmallowOrGreater(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }


  /*  boolean doubleBackToExitPressed = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressed) {
            finish();
            System.exit(0);
        }

        this.doubleBackToExitPressed = true;
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressed =false;
            }
        }, 2000);
    }*/



}
