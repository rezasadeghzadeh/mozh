package ir.sadeghzadeh.mozhdegani.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.MainActivity;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.dialog.ChooseOneItemDialog;
import ir.sadeghzadeh.mozhdegani.dialog.OnOneItemSelectedInDialog;
import ir.sadeghzadeh.mozhdegani.entity.Category;
import ir.sadeghzadeh.mozhdegani.entity.City;
import ir.sadeghzadeh.mozhdegani.entity.KeyValuePair;
import ir.sadeghzadeh.mozhdegani.entity.Province;
import ir.sadeghzadeh.mozhdegani.volley.GsonRequest;

/**
 * Created by reza on 11/2/16.
 */
public class SearchFragment extends BaseFragment {
    public static final String TAG="SearchFragment";
    String currentCategoryId;
    MainActivity activity;
    Button openCategoryPopup;
    Button submit;
    EditText title;
    Button selectProvince;
    Button selectCity;
    RadioGroup radioGroup;
    String selectedCityId;
    String selectedCityTitle;
    String selectedProvinceId;
    String  selectedProvideTitle;
    int selectedItemType;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activity  = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity.highlightSearchIcon();
        View view = layoutInflater.inflate(R.layout.search_fragment, container, false);
        initItemTypes(view);
        initTitle(view);
        initSelectCategory(view);
        initSelectProvince(view);
        initSelectCity(view);
        initSubmit(view);
        return view;
    }

    private void initItemTypes(View view) {
        radioGroup  = (RadioGroup) view.findViewById(R.id.type_radio_group);
        selectedItemType = Const.LOST;
    }

    private void initTitle(View view) {
        title = (EditText) view.findViewById(R.id.title);
    }

    private void initSelectCity(View view) {
        selectCity = (Button) view.findViewById(R.id.select_city);
        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseOneItemDialog dialog = new ChooseOneItemDialog();
                List<City> cities = activity.databaseHandler.getCities(selectedProvinceId);
                List<KeyValuePair> citiesKeyValuePair  =  new ArrayList<KeyValuePair>();
                for(City c: cities){
                    KeyValuePair keyValuePair = new KeyValuePair(c.id,c.name);
                    citiesKeyValuePair.add(keyValuePair);
                }
                dialog.setArguments(citiesKeyValuePair, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedCityId = selectedId;
                        selectedCityTitle = selectedTitle;
                        selectCity.setText(selectedCityTitle);
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);
            }
        });
    }

    private void initSelectProvince(View view) {
        selectProvince = (Button) view.findViewById(R.id.select_province);
        selectProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseOneItemDialog dialog = new ChooseOneItemDialog();
                List<Province> provinceList = activity.databaseHandler.getProvinces();
                List<KeyValuePair> items = new ArrayList<KeyValuePair>();
                for(Province p: provinceList){
                    KeyValuePair keyValuePair = new KeyValuePair(String.valueOf(p.id),p.name);
                    items.add(keyValuePair);
                }
                dialog.setArguments(items, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedProvinceId = selectedId;
                        selectedProvideTitle = selectedTitle;
                        selectProvince.setText(selectedProvideTitle);
                        selectCity.setEnabled(true);
                        //if already selected a city
                        if(selectedCityId != null){
                            selectedCityTitle = getString(R.string.select_city);
                            selectedCityId = "";
                            selectCity.setText(selectedCityTitle);
                        }
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);
            }
        });

    }

    private void initSelectCategory(View view) {
        openCategoryPopup  = (Button) view.findViewById(R.id.openCategoryPopup);
        openCategoryPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.getInstance().addToRequestQueue(
                        new GsonRequest(Const.LIST_CATEGORY_URL, Category[].class, null,null, new Response.Listener<Category[]>() {
                            @Override
                            public void onResponse(Category[] categories) {
                                ChooseOneItemDialog dialog = new ChooseOneItemDialog();
                                List<KeyValuePair> keyValuePairsCategories  = new ArrayList<KeyValuePair>();
                                for(Category c: categories){
                                    KeyValuePair keyValuePair = new KeyValuePair(c.Id,c.Title);
                                    keyValuePairsCategories.add(keyValuePair);
                                }

                                dialog.setArguments(keyValuePairsCategories, new OnOneItemSelectedInDialog() {
                                    @Override
                                    public void onItemSelected(String selectedId, String selectedTitle) {
                                        currentCategoryId = selectedId;
                                        openCategoryPopup.setText(selectedTitle);

                                    }
                                });
                                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(),getString(R.string.connection_error),Toast.LENGTH_LONG).show();
                            }
                        }));
            }
        });
    }

    private void initSubmit(View view) {
        submit = (Button) view.findViewById(R.id.submit);


        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int selectedTypeId  =  radioGroup.getCheckedRadioButtonId();
                if(selectedTypeId  ==  R.id.found){
                    selectedItemType = Const.FOUND;
                }

                BrowseFragment browseFragment = new BrowseFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Const.CATEGORY,currentCategoryId);
                bundle.putString(Const.PROVINCE_ID,selectedProvinceId);
                bundle.putString(Const.CITY_ID,selectedCityId);
                bundle.putString(Const.TITLE,title.getText().toString());
                bundle.putString(Const.ITEM_TYPE,String.valueOf(selectedItemType));
                browseFragment.setArguments(bundle);
                activity.addFragmentToContainer(browseFragment,TAG);
            }
        });
    }
}
