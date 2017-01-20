package ir.sadeghzadeh.mozhdeh.fragment;

import android.Manifest;
import android.location.Location;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.http.entity.mime.content.StringBody;

import java.util.ArrayList;
import java.util.List;

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.dialog.ChooseItemsDialog;
import ir.sadeghzadeh.mozhdeh.dialog.ChooseLocationOnMapDialog;
import ir.sadeghzadeh.mozhdeh.dialog.OnOneItemSelectedInDialog;
import ir.sadeghzadeh.mozhdeh.entity.Category;
import ir.sadeghzadeh.mozhdeh.entity.City;
import ir.sadeghzadeh.mozhdeh.entity.KeyValuePair;
import ir.sadeghzadeh.mozhdeh.entity.Province;
import ir.sadeghzadeh.mozhdeh.utils.Util;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by reza on 11/2/16.
 */
@RuntimePermissions
public class SearchFragment extends BaseFragment implements ChooseLocationOnMapDialog.OnLocationChoosed {
    public static final String TAG = "SearchFragment";
    List<String> currentCategoryIds = new ArrayList<>();
    List<String> currentCategoryTitles = new ArrayList<>();
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
    String selectedProvideTitle;
    int selectedItemType;
    private String selectedAddress;
    private String latitude;
    private String longitude;
    Button showMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        activity.closeKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity.highlightSearchIcon();
        View view = layoutInflater.inflate(R.layout.search_fragment, container, false);
        initItemTypes(view);
        initTitle(view);
        initShowMap(view);
        initSelectCategory(view);
        initSelectProvince(view);
        initSelectCity(view);
        initSubmit(view);
        initBackButton();
        animate(view.findViewById(R.id.main_layout));

        return view;
    }


    private void initBackButton() {
        activity.backButton.setVisibility(View.GONE);
    }

    private void initItemTypes(View view) {
        radioGroup = (RadioGroup) view.findViewById(R.id.type_radio_group);
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
                ChooseItemsDialog dialog = new ChooseItemsDialog();
                List<City> cities = activity.databaseHandler.getCities(selectedProvinceId);
                List<KeyValuePair> citiesKeyValuePair = new ArrayList<KeyValuePair>();
                for (City c : cities) {
                    KeyValuePair keyValuePair = new KeyValuePair(c.id, c.name);
                    citiesKeyValuePair.add(keyValuePair);
                }
                /*dialog.setArguments(citiesKeyValuePair, new OnOneItemSelectedInDialog() {
                    @Override
                    public void onItemSelected(String selectedId, String selectedTitle) {
                        selectedCityId = selectedId;
                        selectedCityTitle = selectedTitle;
                        selectCity.setText(selectedCityTitle);
                    }
                });
                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);*/
            }
        });
    }

    private void initSelectProvince(View view) {
        selectProvince = (Button) view.findViewById(R.id.select_province);
        selectProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseItemsDialog dialog = new ChooseItemsDialog();
                List<Province> provinceList = activity.databaseHandler.getProvinces();
                List<KeyValuePair> items = new ArrayList<KeyValuePair>();
                for (Province p : provinceList) {
                    KeyValuePair keyValuePair = new KeyValuePair(String.valueOf(p.id), p.name);
                    items.add(keyValuePair);
                }
              /*  dialog.setArguments(items, new OnOneItemSelectedInDialog() {
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
                dialog.show(activity.getSupportFragmentManager().beginTransaction(),TAG);*/
            }
        });

    }

    private void initSelectCategory(View view) {
        openCategoryPopup = (Button) view.findViewById(R.id.openCategoryPopup);
        openCategoryPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationController.getInstance().addToRequestQueue(
                        new GsonRequest(Const.LIST_CATEGORY_URL, Category[].class, null, null, new Response.Listener<Category[]>() {
                            @Override
                            public void onResponse(Category[] categories) {
                                ChooseItemsDialog dialog = new ChooseItemsDialog();
                                List<KeyValuePair> keyValuePairsCategories = new ArrayList<KeyValuePair>();
                                for (Category c : categories) {
                                    KeyValuePair keyValuePair = new KeyValuePair(c.Id, c.Title);
                                    keyValuePairsCategories.add(keyValuePair);
                                }

                                dialog.setArguments(keyValuePairsCategories, true, new OnOneItemSelectedInDialog() {
                                    @Override
                                    public void onItemSelected(List<KeyValuePair> selected) {
                                        currentCategoryIds.clear();
                                        currentCategoryTitles.clear();
                                        for (KeyValuePair pair : selected) {
                                            currentCategoryIds.add(pair.key);
                                            currentCategoryTitles.add(pair.value);
                                        }
                                        if (currentCategoryIds.size() > 0) {
                                            openCategoryPopup.setText(Util.buildCommaSeperate(currentCategoryTitles));
                                        } else {
                                            openCategoryPopup.setText(getString(R.string.select));
                                        }
                                    }
                                });
                                dialog.show(activity.getSupportFragmentManager().beginTransaction(), TAG);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
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
                final int selectedTypeId = radioGroup.getCheckedRadioButtonId();
                if (selectedTypeId == R.id.found) {
                    selectedItemType = Const.FOUND;
                }

                BrowseFragment browseFragment = new BrowseFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Const.CATEGORIES, Util.buildCommaSeperate(currentCategoryIds));
                bundle.putString(Const.PROVINCE_ID, selectedProvinceId);
                bundle.putString(Const.CITY_ID, selectedCityId);
                bundle.putString(Const.TITLE, title.getText().toString());
                bundle.putString(Const.ITEM_TYPE, String.valueOf(selectedItemType));
                if (latitude != null && !latitude.isEmpty()) {
                    bundle.putString(Const.LATITUDE, latitude);
                    bundle.putString(Const.LONGITUDE, longitude);
                }
                browseFragment.setArguments(bundle);
                activity.addFragmentToContainer(browseFragment, TAG, true);
            }
        });
    }

    private void initShowMap(View view) {
        showMap = (Button) view.findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showProgress();
                SearchFragmentPermissionsDispatcher.showMapInSearchFragmentWithCheck(SearchFragment.this);
            }
        });
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void showMapInSearchFragment() {
        ChooseLocationOnMapDialog dialog = new ChooseLocationOnMapDialog();
        dialog.mListener = this;
        dialog.show(activity.getSupportFragmentManager(), TAG);
    }

    @Override
    public void onLocationChoosed(Location location, String address) {
        this.latitude = String.valueOf(location.getLatitude());
        this.longitude = String.valueOf(location.getLongitude());
        this.selectedAddress = address;
        showMap.setText(address);
    }

}
