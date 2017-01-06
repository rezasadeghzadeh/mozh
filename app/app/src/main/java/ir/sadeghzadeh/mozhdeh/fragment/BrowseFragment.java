package ir.sadeghzadeh.mozhdeh.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.adapter.ItemsAdapter;
import ir.sadeghzadeh.mozhdeh.entity.Item;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;

/**
 * Created by reza on 11/1/16.
 */
public class BrowseFragment extends BaseFragment {

    public static String TAG =  "BrowseFragment";
    ListView itemsListView;
    TextView message;
    Bundle args;
    String title;
    String itemType;
    String selectedCityId;
    String selectedCityTitle;
    String selectedProvinceId;
    String selectedProvideTitle;
    String currentCategoryId;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        args  =  getArguments();
        activity.highlightHomeIcon();
        View view = layoutInflater.inflate(R.layout.browse_fragment, container, false);
        initItemsListView(view);
        initBackButton();
        showItems();
        return view;
    }

    private void initBackButton() {
        activity.backButton.setVisibility(View.GONE);
    }

    private void initItemsListView(View view) {
        itemsListView = (ListView) view.findViewById(R.id.items_list_view);
        message  = (TextView) view.findViewById(R.id.message);
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                activity.showProgress();
                String selectedItemId =  view.getTag().toString();
                Bundle args  =  new Bundle();
                args.putString(Const.ID,selectedItemId);
                DetailItemFragment fragment  = new DetailItemFragment();
                fragment.setArguments(args);
                activity.addFragmentToContainer(fragment,TAG);
            }
        });
    }

    private void showItems() {
        activity.showProgress();
        HashMap<String,String> params = new HashMap<>();
        if(args!= null){
            if(args.getString(Const.TITLE) != null && !args.getString(Const.TITLE).isEmpty() ){
                title  =  args.getString(Const.TITLE);
                params.put(Const.TITLE,title);
            }
            if(args.getString(Const.CATEGORIES) != null  && !args.getString(Const.CATEGORIES).isEmpty()){
                currentCategoryId  =  args.getString(Const.CATEGORIES);
                params.put(Const.CATEGORIES,currentCategoryId);
            }
            if(args.getString(Const.PROVINCE_ID)  !=  null  &&  !args.getString(Const.PROVINCE_ID).isEmpty()){
                selectedProvinceId  =  args.getString(Const.PROVINCE_ID);
                params.put(Const.PROVINCE_ID,selectedProvinceId);
            }
            if(args.getString(Const.CITY_ID) != null &&  !args.getString(Const.CITY_ID).isEmpty() ){
                selectedCityId  =  args.getString(Const.CITY_ID);
                params.put(Const.CITY_ID,selectedCityId);
            }
            if(args.getString(Const.ITEM_TYPE) != null &&  !args.getString(Const.ITEM_TYPE).isEmpty() ){
                itemType = args.getString(Const.ITEM_TYPE);
                params.put(Const.ITEM_TYPE,itemType);
            }
        }
        params.put(Const.APPROVED,"true");

        ApplicationController.getInstance().addToRequestQueue(
                new GsonRequest(Const.LIST_ITEMS_URL, Item[].class, params,null, new Response.Listener<Item[]>() {
                    @Override
                    public void onResponse(Item[] response) {
                        activity.hideProgress();
                        if(response == null){
                            message.setText(getString(R.string.no_item_found));
                            message.setVisibility(View.VISIBLE);
                            return;
                        }
                        itemsListView.setAdapter(new ItemsAdapter(getContext(),0,response,activity,false));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.hideProgress();
                        Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){

                }
        );

    }

}
