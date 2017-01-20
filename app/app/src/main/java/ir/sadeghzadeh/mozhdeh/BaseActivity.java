package ir.sadeghzadeh.mozhdeh;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.EmptyStackException;

import ir.sadeghzadeh.mozhdeh.utils.SizedStack;

/**
 * Created by reza on 11/2/16.
 */
public class BaseActivity extends AppCompatActivity {
    SizedStack<String> backStack = new SizedStack<>(5);
    public void addFragmentToContainer(Fragment fragment, String tag, boolean addToBackStack) {
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
            if(addToBackStack){
                fragmentTransaction.addToBackStack(tag);
            }

            fragmentTransaction.commit();
            backStack.push(tag);
        } catch (Exception e) {
            Log.e("addFragmentToContainer", e.toString());
        }
    }

    public boolean isMarshmallowOrGreater(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onBackPressed(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean closed = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if(closed){
                return;
            }
        }

        try {
            if(backStack.pop() != null){
                super.onBackPressed();
            }else {
                finish();
                System.exit(0);
            }
        }catch (EmptyStackException e){
            finish();
            System.exit(0);
        }
    }



/*
    boolean doubleBackToExitPressed = false;

    @Override
    public void onBackPressed() {
*/
/*        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            return;
        }*//*


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
    }
*/



}
