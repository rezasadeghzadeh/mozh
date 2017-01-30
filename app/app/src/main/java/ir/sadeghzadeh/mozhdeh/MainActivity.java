package ir.sadeghzadeh.mozhdeh;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loveplusplus.update.UpdateChecker;

import java.util.ArrayList;
import java.util.Locale;

import ir.sadeghzadeh.mozhdeh.fragment.BrowseFragment;
import ir.sadeghzadeh.mozhdeh.fragment.CategoryFragment;
import ir.sadeghzadeh.mozhdeh.fragment.DetailItemFragment;
import ir.sadeghzadeh.mozhdeh.fragment.EnterEmailOrMobileFragment;
import ir.sadeghzadeh.mozhdeh.fragment.LoginFragment;
import ir.sadeghzadeh.mozhdeh.fragment.MyItemsFragment;
import ir.sadeghzadeh.mozhdeh.fragment.NewFragment;
import ir.sadeghzadeh.mozhdeh.fragment.SearchFragment;
import ir.sadeghzadeh.mozhdeh.utils.ExceptionHandler;
import ir.sadeghzadeh.mozhdeh.utils.SizedStack;
import ir.sadeghzadeh.mozhdeh.utils.Util;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getName();
    Button homeButton;
    Button searchButton;
    Button categoryButton;
    Button newButton;
    public Button backButton;

    Button myItemsButton;
    public DatabaseHandler databaseHandler;
    ProgressDialog progress;
    private TextView title;
    public boolean writeExternalStorageGranted;
    public boolean readExternalStorageGranted;

    private final static int WRITE_EXTERNAL_STORAGE_RESULT =101;
    private final static int READ_EXTERNAL_STORAGE_RESULT = 102;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            //registerExceptionHandler();
            initUtil();
            MainActivityPermissionsDispatcher.writeVersionToLogWithCheck(this);
            UpdateChecker.checkForDialog(this);
            setContentView(R.layout.activity_main);

            //setDefaultLanguage();
            initCustomActionBar();
            initBackButton();
            initElements();

            //check if passed an id, we should open detail fragment
            String id =  getIntent().getStringExtra(Const.ID);
            if(id != null){
                Bundle args  =  new Bundle();
                args.putString(Const.ID,id);
                DetailItemFragment fragment  = new DetailItemFragment();
                fragment.setArguments(args);
                addFragmentToContainer(fragment,DetailItemFragment.TAG, true);
            }else {
                addFragmentToContainer(new BrowseFragment(), BrowseFragment.TAG, false);
            }

            //openDatabase();
            initProgress();
            showProgress();

    }

    private void initBackButton() {
        backButton = (Button) findViewById(R.id.back);
        backButton.setVisibility(View.GONE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void initUtil() {
        Util.context  = getApplicationContext();
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA})
    public void writeVersionToLog(){
        Util.writeToLogFile("Version: " + Const.VERSION);
    }


    private void initCustomActionBar() {
        // Inflate your custom layout
        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.custom_action_bar, null);

        // Set up your ActionBar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        title  = (TextView) actionBarLayout.findViewById(R.id.page_title);
    }


    public void initProgress(){
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.please_wait));
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }

    public void showProgress(){
        progress.show();
    }

    public void hideProgress(){
        progress.dismiss();
    }

    private void openDatabase() {
        try {
            databaseHandler = new DatabaseHandler(getApplicationContext());
            databaseHandler.createDatabase();
            databaseHandler.openDataBase();
        }catch (Exception e){
            Util.writeToLogFile("Opening Database:",e.getMessage());
        }
    }

    private void registerExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    }

    private void initElements() {
        homeButton = (Button) findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new BrowseFragment(),BrowseFragment.TAG, true);
            }
        });
        categoryButton = (Button) findViewById(R.id.category_button);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new CategoryFragment(),BrowseFragment.TAG, true);
            }
        });
        newButton = (Button) findViewById(R.id.new_bbutton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.isUserLogged()){
                    addFragmentToContainer(new NewFragment(), NewFragment.TAG, true);
                }else {
                    Bundle args  = new Bundle();
                    args.putBoolean(Const.REDIRECT_TO_NEW,true);
                    LoginFragment fragment  = new LoginFragment();
                    fragment.setArguments(args);
                    addFragmentToContainer(fragment, LoginFragment.LOGIN_BEFORE_NEW_TAG, true);
                    highlightNewIcon();
                    setTitleToAuth();
                }

            }
        });
        myItemsButton = (Button) findViewById(R.id.my_items_button);
        myItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.isUserLogged()){
                    addFragmentToContainer(new MyItemsFragment(), MyItemsFragment.TAG, true);
                }else {
                    Bundle args = new Bundle();
                    args.putBoolean(Const.REDIRECT_TO_MY,true);
                    LoginFragment  fragment  = new LoginFragment();
                    fragment.setArguments(args);
                    addFragmentToContainer(fragment, LoginFragment.LOGIN_BEFORE_MY_TAG, true);
                    highlightMyItemsIcon();
                    setTitleToAuth();
                }
            }
        });
        searchButton = (Button) findViewById(R.id.search_items_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new SearchFragment(), SearchFragment.TAG, true);
            }
        });

    }

    private void setDefaultLanguage() {
        Locale locale = new Locale("fa_IR");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);

    }

    public void highlightHomeIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_white);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.home));
    }

    public void highlightSearchIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_white);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.search));
    }

    public void highlightMyItemsIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_white);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.my_items));
    }

    public void highlightCategoryIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_white);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.category));
    }

    public void highlightNewIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_white);
        setTitle(getString(R.string.new_item));
    }

    public void setTitleToAuth() {
        setTitle(getString(R.string.login_to_accout));
    }

   @Override
    public void setTitle(CharSequence string) {
        title.setText(string);
    }



    /**
     * This is the method that is hit after the user accepts/declines the
     * permission you requested. For the purpose of this example I am showing a "success" header
     * when the user accepts the permission and a snackbar when the user declines it.  In your application
     * you will want to handle the accept/decline in a way that makes sense.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_RESULT:
                if(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    writeExternalStorageGranted =  true;
                }else{
                    permissionsRejected.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    writeExternalStorageGranted = false;
                }
                break;
            case READ_EXTERNAL_STORAGE_RESULT:
                if(hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    readExternalStorageGranted = true;
                }else{
                    permissionsRejected.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    readExternalStorageGranted = false;
                }
                break;
        }

    }


    /**
     * method that will return whether the permission is accepted. By default it is true if the user is using a device below
     * version 23
     * @param permission
     * @return
     */
    private boolean hasPermission(String permission) {
        if (isMarshmallowOrGreater()) {
            return( ContextCompat.checkSelfPermission(getApplicationContext(),permission)== PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    /**
     * method to determine whether we have asked
     * for this permission before.. if we have, we do not want to ask again.
     * They either rejected us or later removed the permission.
     * @param permission
     * @return
     */
    private boolean shouldWeAsk(String permission) {
        return Util.fetchBooleanFromPreferences(permission,true);
    }

    /**
     * we will save that we have already asked the user
     * @param permission
     */
    private void markAsAsked(String permission) {
        Util.saveBooleanInPreferences(permission, false);
    }

    /**
     * We may want to ask the user again at their request.. Let's clear the
     * marked as seen preference for that permission.
     * @param permission
     */
    private void clearMarkAsAsked(String permission) {
        Util.saveBooleanInPreferences(permission, true);
    }


    /**
     * This method is used to determine the permissions we do not have accepted yet and ones that we have not already
     * bugged the user about.  This comes in handle when you are asking for multiple permissions at once.
     * @param wanted
     * @return
     */
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * this will return us all the permissions we have previously asked for but
     * currently do not have permission to use. This may be because they declined us
     * or later revoked our permission. This becomes useful when you want to tell the user
     * what permissions they declined and why they cannot use a feature.
     * @param wanted
     * @return
     */
    private ArrayList<String> findRejectedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm) && !shouldWeAsk(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}

