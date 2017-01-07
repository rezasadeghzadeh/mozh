package ir.sadeghzadeh.mozhdeh.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.adapter.ItemsAdapter;
import ir.sadeghzadeh.mozhdeh.entity.Item;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;

/**
 * Created by reza on 11/2/16.
 */
public class MyItemsFragment extends BaseFragment {
    public static final String TAG = "MyItemsFragment";

    ListView itemsListView;
    TextView message;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        animate(view.findViewById(R.id.main_layout));

        return view;
    }

    private void initMessage(View view) {
        message = (TextView) view.findViewById(R.id.message);
    }

    private void showItems() {
        activity.showProgress();
        ApplicationController.getInstance().addToRequestQueue(
                new GsonRequest(Const.MY_ITEMS_URL, Item[].class, null, null, new Response.Listener<Item[]>() {
                    @Override
                    public void onResponse(Item[] response) {
                        activity.hideProgress();
                        if (response == null) {
                            message.setText(getString(R.string.no_item_found));
                            message.setVisibility(View.VISIBLE);
                            return;
                        }
                        itemsListView.setAdapter(new ItemsAdapter(getContext(), 0, response, activity, true));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.hideProgress();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

                }
        );


    }

    private void initItemsListView(View view) {
        itemsListView = (ListView) view.findViewById(R.id.items_list_view);
    }


}
