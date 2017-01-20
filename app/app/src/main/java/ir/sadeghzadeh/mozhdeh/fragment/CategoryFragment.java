package ir.sadeghzadeh.mozhdeh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.adapter.CategoryAdapter;
import ir.sadeghzadeh.mozhdeh.entity.Category;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;

/**
 * Created by reza on 11/2/16.
 */
public class CategoryFragment extends BaseFragment {
    public static final String TAG = "CategoryFragment";

    MainActivity activity;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        activity.closeKeyboard();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.category_fragment, container, false);
        initListView(view);
        initBackButton();
        animate(view.findViewById(R.id.main_layout));
        activity.highlightCategoryIcon();

        return view;
    }

    private void initBackButton() {
        activity.backButton.setVisibility(View.GONE);
    }

    private void initListView(View view) {
        activity.showProgress();
        listView = (ListView) view.findViewById(R.id.category_list);
        GsonRequest<Category[]> request = new GsonRequest<>(Const.LIST_CATEGORY_URL, Category[].class,
                null, null, new Response.Listener<Category[]>() {
            @Override
            public void onResponse(Category[] response) {
                listView.setAdapter(new CategoryAdapter(getContext(), 0, response));
                activity.hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                activity.hideProgress();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        ApplicationController.getInstance().addToRequestQueue(request);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] rowValues = view.getTag().toString().split(",");
                Bundle args = new Bundle();
                args.putString(Const.CATEGORIES, rowValues[0]);
                Fragment browseFragment = new BrowseFragment();
                browseFragment.setArguments(args);
                activity.addFragmentToContainer(browseFragment, TAG, true);
            }
        });
    }

}
