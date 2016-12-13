package ir.sadeghzadeh.mozhdegani.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.MainActivity;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.adapter.ItemsAdapter;
import ir.sadeghzadeh.mozhdegani.entity.Item;
import ir.sadeghzadeh.mozhdegani.volley.GsonRequest;

/**
 * Created by reza on 11/2/16.
 */
public class MyItemsFragment extends BaseFragment {
    public static final String TAG="MyItemsFragment";

    ListView itemsListView;
    TextView message;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity.highlightMyItemsIcon();
        View view = layoutInflater.inflate(R.layout.my_items_fragment, container, false);
        initMessage(view);
        initItemsListView(view);
        showItems();
        return view;
    }

    private void initMessage(View view) {
        message = (TextView) view.findViewById(R.id.message);
    }

    private void showItems() {
        activity.showProgress();
        ApplicationController.getInstance().addToRequestQueue(
                new GsonRequest(Const.MY_ITEMS_URL, Item[].class, null,null, new Response.Listener<Item[]>() {
                    @Override
                    public void onResponse(Item[] response) {
                        activity.hideProgress();
                        if(response == null){
                            message.setText(getString(R.string.no_item_found));
                            message.setVisibility(View.VISIBLE);
                            return;
                        }
                        itemsListView.setAdapter(new ItemsAdapter(getContext(),0,response,activity,true));
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

    private void initItemsListView(View view) {
        itemsListView = (ListView) view.findViewById(R.id.items_list_view);
    }


}
