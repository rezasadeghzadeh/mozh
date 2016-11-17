package ir.sadeghzadeh.mozhdegani.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.MainActivity;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.entity.Item;
import ir.sadeghzadeh.mozhdegani.volley.GsonRequest;

/**
 * Created by reza on 11/14/16.
 */
public class DetailItemFragment extends BaseFragment{
    static final String TAG  =  DetailItemFragment.class.getName();
    private MainActivity activity;
    private String  id;
    TextView title;
    TextView description;
    TextView date;
    TextView city;
    NetworkImageView itemImage;
    TextView category;
    TextView mobile;
    TextView lost;
    TextView founded;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activity  = (MainActivity) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.detail_item_fragment, container, false);

        Bundle args = getArguments();
        if(args != null  && !args.getString(Const.ID).isEmpty()){
            id  =  args.getString(Const.ID);
            initDetails(view);
        }

        return view;
    }

    private void initDetails(View view) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ID,id);

        title = (TextView) view.findViewById(R.id.item_title);
        category = (TextView) view.findViewById(R.id.category);
        description = (TextView) view.findViewById(R.id.item_description);
        date = (TextView) view.findViewById(R.id.date);
        city = (TextView) view.findViewById(R.id.city);
        itemImage = (NetworkImageView) view.findViewById(R.id.item_image);
        mobile = (TextView) view.findViewById(R.id.mobile);
        lost = (TextView) view.findViewById(R.id.lost_type);
        founded = (TextView) view.findViewById(R.id.founded_type);
        GsonRequest<Item> request = new GsonRequest<>(Const.DETAIL_ITEM_URL, Item.class, params, null, new Response.Listener<Item>() {
            @Override
            public void onResponse(Item item) {
                //set values
                title.setText(item.Title);
                category.setText(String.valueOf(item.CategoryTitle));
                mobile.setText(item.Mobile);
                description.setText(item.Description);
                date.setText(item.Date);
                city.setText(String.valueOf(item.CityTitle));
                if(item.ImageExt != null && !item.ImageExt.isEmpty()){
                    String uri = Const.SERVER_URL + Const.FULL_IMAGE_URL + "/" + item.id + item.ImageExt;
                    itemImage.setImageUrl(uri, ApplicationController.getInstance().getImageLoaderInstance());
                }

                if(item.ItemType.equals(Const.FOUND+"")){
                    founded.setVisibility(View.VISIBLE);
                    lost.setVisibility(View.GONE);
                }else if(item.ItemType.equals(Const.LOST+"")){
                    founded.setVisibility(View.GONE);
                    lost.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,error.toString());
            }
        });
        ApplicationController.getInstance().addToRequestQueue(request);
    }

}
