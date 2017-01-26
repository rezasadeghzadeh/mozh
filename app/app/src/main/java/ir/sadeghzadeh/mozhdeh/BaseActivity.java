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

import ir.sadeghzadeh.mozhdeh.fragment.LoginFragment;
import ir.sadeghzadeh.mozhdeh.fragment.MyItemsFragment;
import ir.sadeghzadeh.mozhdeh.fragment.NewFragment;
import ir.sadeghzadeh.mozhdeh.utils.SizedStack;
import ir.sadeghzadeh.mozhdeh.utils.Util;

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
                backStack.push(tag);
            }
            fragmentTransaction.commit();
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


    boolean doubleBackToExitPressed = false;

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

        String tag=null;
        try {
            tag  =  backStack.pop();
        }catch (EmptyStackException e){

        }
        if(tag != null){
              /*  if(!Util.isUserLogged()){
                    super.onBackPressed();
                    return;
                }
                //we dont want to display login page  to user if he/she  has logged
                switch (tag){
                    case LoginFragment.LOGIN_BEFORE_MY_TAG:
                        getSupportFragmentManager().popBackStackImmediate();
                        addFragmentToContainer(new NewFragment(), MyItemsFragment.TAG, false);
                        return;
                    case LoginFragment.LOGIN_BEFORE_NEW_TAG:
                        getSupportFragmentManager().popBackStackImmediate();
                        addFragmentToContainer(new NewFragment(), NewFragment.TAG, false);
                        return;
                }*/
            super.onBackPressed();
        }else {
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
