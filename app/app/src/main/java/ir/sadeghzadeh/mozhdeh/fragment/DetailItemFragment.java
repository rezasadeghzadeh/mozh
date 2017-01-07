package ir.sadeghzadeh.mozhdeh.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.entity.Item;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;

/**
 * Created by reza on 11/14/16.
 */
public class DetailItemFragment extends BaseFragment implements OnMapReadyCallback {
    public static final String TAG = DetailItemFragment.class.getName();
    private static View view;
    TextView title;
    TextView description;
    TextView date;
    TextView address;
    NetworkImageView itemImage;
    TextView category;
    TextView mobile;
    TextView lost;
    TextView founded;
    TextView email;
    TextView telegramId;
    Button sendMessage;
    Item item;
    SupportMapFragment mapFragment;
    View mapLayout;
    String uri;
    private MainActivity activity;
    private String id;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }

        view = layoutInflater.inflate(R.layout.detail_item_fragment, container, false);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map_container);
        mapLayout = view.findViewById(R.id.map_layout);
        Bundle args = getArguments();
        if (args != null && !args.getString(Const.ID).isEmpty()) {
            id = args.getString(Const.ID);
            initDetails(view);
            initFullScreenImage();
        }
        initBackButton();
        initSendMessageButton(view);
        animate(view.findViewById(R.id.main_layout));

        return view;
    }

    private void initSendMessageButton(View view) {
        sendMessage = (Button) view.findViewById(R.id.send_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(Const.ID, id);
                SendMessageFragment fragment = new SendMessageFragment();
                fragment.setArguments(args);
                activity.addFragmentToContainer(fragment, SendMessageFragment.TAG);
            }
        });
    }

    private void initBackButton() {
        activity.backButton.setVisibility(View.VISIBLE);
    }

    private void initFullScreenImage() {
        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.full_screen_image, null);
                builder.setView(view);
                NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.item_image);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                ((Activity) getContext()).getWindowManager()
                        .getDefaultDisplay()
                        .getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;
                imageView.setMaxWidth(width);
                imageView.setMaxHeight(height);
                imageView.setImageUrl(uri, ApplicationController.getInstance().getImageLoaderInstance());
                final Dialog dialog = builder.create();
                dialog.show();
                View itemImageContainer = view.findViewById(R.id.item_image_container);
                Button exit = (Button) view.findViewById(R.id.exit_button);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };
                exit.setOnClickListener(listener);
                itemImageContainer.setOnClickListener(listener);
            }
        });
    }

    private void initDetails(View view) {
        Map<String, String> params = new HashMap<>();
        params.put(Const.ID, id);

        title = (TextView) view.findViewById(R.id.item_title);
        category = (TextView) view.findViewById(R.id.category);
        description = (TextView) view.findViewById(R.id.item_description);
        date = (TextView) view.findViewById(R.id.date);
        address = (TextView) view.findViewById(R.id.address);
        itemImage = (NetworkImageView) view.findViewById(R.id.item_image);
        //mobile = (TextView) view.findViewById(R.id.mobile);
        lost = (TextView) view.findViewById(R.id.lost_type);
        founded = (TextView) view.findViewById(R.id.founded_type);
        //email  = (TextView) view.findViewById(R.id.email);
        //telegramId = (TextView) view.findViewById(R.id.telegram_id);

        GsonRequest<Item> request = new GsonRequest<>(Const.DETAIL_ITEM_URL, Item.class, params, null, new Response.Listener<Item>() {
            @Override
            public void onResponse(Item item) {
                DetailItemFragment.this.item = item;
                //set values
                title.setText(item.Title);
                category.setText(String.valueOf(item.CategoryTitles));
                //mobile.setText(item.Mobile);
                //email.setText(item.Email);
                //telegramId.setText(item.TelegramId);
                description.setText(item.Description);
                date.setText(item.Date);
                address.setText(String.valueOf(item.Address));
                if (item.ImageExt != null && !item.ImageExt.isEmpty()) {
                    uri = Const.SERVER_URL + Const.FULL_IMAGE_URL + "/" + item.id + item.ImageExt;
                    itemImage.setImageUrl(uri, ApplicationController.getInstance().getImageLoaderInstance());
                }

                if (item.ItemType.equals(Const.FOUND + "")) {
                    founded.setVisibility(View.VISIBLE);
                    lost.setVisibility(View.GONE);
                } else if (item.ItemType.equals(Const.LOST + "")) {
                    founded.setVisibility(View.GONE);
                    lost.setVisibility(View.VISIBLE);
                }
                if (item.Latitude != null && !item.Latitude.isEmpty() && item.Longitude != null && !item.Longitude.isEmpty()) {
                    initMap();
                } else {
                    mapLayout.setVisibility(View.GONE);
                }
                activity.hideProgress();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        ApplicationController.getInstance().addToRequestQueue(request);
    }

    private void initMap() {
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentByTag("mapFragment");
        if (mapFragment == null) {
            mapFragment = new SupportMapFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.map_layout, mapFragment, "mapFragment");
            ft.commit();
            fm.executePendingTransactions();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng point = new LatLng(Double.parseDouble(item.Latitude), Double.parseDouble(item.Longitude));
        mMap.addMarker(new MarkerOptions().position(point).title("Lost/Found Place"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
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
