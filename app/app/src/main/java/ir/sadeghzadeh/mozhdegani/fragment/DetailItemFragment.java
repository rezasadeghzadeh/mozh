package ir.sadeghzadeh.mozhdegani.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class DetailItemFragment extends BaseFragment implements OnMapReadyCallback {
    static final String TAG  =  DetailItemFragment.class.getName();
    private MainActivity activity;
    private String  id;
    TextView title;
    TextView description;
    TextView date;
    TextView address;
    NetworkImageView itemImage;
    TextView category;
    TextView mobile;
    TextView lost;
    TextView founded;
    private GoogleMap mMap;
    Item item;
    SupportMapFragment mapFragment;
    View mapLayout;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activity  = (MainActivity) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.detail_item_fragment, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_container);
        mapLayout= view.findViewById(R.id.map_layout);
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
        address = (TextView) view.findViewById(R.id.address);
        itemImage = (NetworkImageView) view.findViewById(R.id.item_image);
        mobile = (TextView) view.findViewById(R.id.mobile);
        lost = (TextView) view.findViewById(R.id.lost_type);
        founded = (TextView) view.findViewById(R.id.founded_type);
        GsonRequest<Item> request = new GsonRequest<>(Const.DETAIL_ITEM_URL, Item.class, params, null, new Response.Listener<Item>() {
            @Override
            public void onResponse(Item item) {
                DetailItemFragment.this.item = item;
                //set values
                title.setText(item.Title);
                category.setText(String.valueOf(item.CategoryTitle));
                mobile.setText(item.Mobile);
                description.setText(item.Description);
                date.setText(item.Date);
                address.setText(String.valueOf(item.Address));
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
                if(item.Latitude !=null && !item.Latitude.isEmpty() &&  item.Longitude!= null &&  !item.Longitude.isEmpty()){
                    initMap();
                }else {
                    mapLayout.setVisibility(View.GONE);
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

    private void initMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng point = new LatLng(Double.parseDouble(item.Latitude), Double.parseDouble(item.Longitude));
        mMap.addMarker(new MarkerOptions().position(point).title("Lost/Found Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

}
