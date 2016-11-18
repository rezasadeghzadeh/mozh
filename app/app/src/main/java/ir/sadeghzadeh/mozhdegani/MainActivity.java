package ir.sadeghzadeh.mozhdegani;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import ir.sadeghzadeh.mozhdegani.fragment.BrowseFragment;
import ir.sadeghzadeh.mozhdegani.fragment.CategoryFragment;
import ir.sadeghzadeh.mozhdegani.fragment.NewFragment;
import ir.sadeghzadeh.mozhdegani.fragment.SearchFragment;
import ir.sadeghzadeh.mozhdegani.utils.ExceptionHandler;
import ir.sadeghzadeh.mozhdegani.utils.Util;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();
    Button homeButton;
    Button searchButton;
    Button categoryButton;
    Button newButton;
    //Button myItemsButton;
    public DatabaseHandler databaseHandler;
    ProgressDialog progress;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.writeToLogFile("Version: " + Const.VERSION);
        registerExceptionHandler();
        //setDefaultLanguage();
        setContentView(R.layout.activity_main);
        initCustomActionBar();
        initElements();
        addFragmentToContainer(new BrowseFragment(),BrowseFragment.TAG);
        openDatabase();
        initProgress();
        showProgress();
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
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
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
                addFragmentToContainer(new BrowseFragment(),BrowseFragment.TAG);
            }
        });
        categoryButton = (Button) findViewById(R.id.category_button);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new CategoryFragment(),BrowseFragment.TAG);
            }
        });
        newButton = (Button) findViewById(R.id.new_bbutton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new NewFragment(), NewFragment.TAG);
            }
        });
      /*  myItemsButton = (Button) findViewById(R.id.my_items_button);
        myItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new MyItemsFragment(), MyItemsFragment.TAG);
            }
        });*/
        searchButton = (Button) findViewById(R.id.search_items_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new SearchFragment(), SearchFragment.TAG);
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
        //myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.home));
    }

    public void highlightSearchIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_white);
        //myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.search));
    }

    public void highlightMyItemsIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        //myItemsButton.setBackgroundResource(R.drawable.ic_my_items_white);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
    }

    public void highlightCategoryIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        //myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_white);
        newButton.setBackgroundResource(R.drawable.ic_new_black);
        setTitle(getString(R.string.category));
    }

    public void highlightNewIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        //myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_white);
        setTitle(getString(R.string.new_item));
    }

   @Override
    public void setTitle(CharSequence string) {
        title.setText(string);
    }

}

