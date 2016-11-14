package ir.sadeghzadeh.mozhdegani;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

import ir.sadeghzadeh.mozhdegani.fragment.BrowseFragment;
import ir.sadeghzadeh.mozhdegani.fragment.CategoryFragment;
import ir.sadeghzadeh.mozhdegani.fragment.MyItemsFragment;
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
    Button myItemsButton;
    public DatabaseHandler databaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.writeToLogFile("Version: " + MyR.VERSION);
        registerExceptionHandler();
        setDefaultLanguage();
        setContentView(R.layout.activity_main);
        initElements();
        addFragmentToContainer(new BrowseFragment(),BrowseFragment.TAG);
        openDatabase();
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
        myItemsButton = (Button) findViewById(R.id.my_items_button);
        myItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragmentToContainer(new MyItemsFragment(), MyItemsFragment.TAG);
            }
        });
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
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);

    }

    public void highlightSearchIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_white);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);    }

    public void highlightMyItemsIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_white);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_black);    }

    public void highlightCategoryIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_white);
        newButton.setBackgroundResource(R.drawable.ic_new_black);    }

    public void highlightNewIcon() {
        homeButton.setBackgroundResource(R.drawable.ic_home_black);
        searchButton.setBackgroundResource(R.drawable.ic_search_black);
        myItemsButton.setBackgroundResource(R.drawable.ic_my_items_black);
        categoryButton.setBackgroundResource(R.drawable.ic_category_black);
        newButton.setBackgroundResource(R.drawable.ic_new_white);
    }

}

