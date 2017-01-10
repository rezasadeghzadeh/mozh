package ir.sadeghzadeh.mozhdeh.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.utils.Util;

/**
 * Created by reza on 11/25/16.
 */
public class ChooseLocationOnMapDialog extends DialogFragment implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    MainActivity activity;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    private Location mLastLocation;
    Fragment fragment;
    public ChooseLocationOnMapDialog(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        fragment  = getParentFragment();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view  = inflater.inflate(R.layout.chose_location_on_map_dialog, null);

        SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);

       builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mResultReceiver = new AddressResultReceiver(new Handler());
                        Intent intent = new Intent(activity, FetchAddressIntentService.class);
                        intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
                        intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, mLastLocation);
                        activity.startService(intent);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });
        builder.setTitle(getString(R.string.touch_to_chose_place));
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) throws SecurityException {
        mMap = googleMap;
        //click listener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mLastLocation  = new Location("");
                mLastLocation.setLatitude(point.latitude);
                mLastLocation.setLongitude(point.longitude);
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(point).title("Lost/Found Place"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(18);
        mMap.animateCamera(zoom);
        activity.hideProgress();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is disabled");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
                ChooseLocationOnMapDialog.this.dismiss();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation  = Util.getLocation();
        if(mLastLocation!= null){
            LatLng currentLatLng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
                String address = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY) ;
                mListener.onLocationChoosed(mLastLocation,address);
            }

        }
    }

    public OnLocationChoosed mListener;
    public interface OnLocationChoosed{
        public void onLocationChoosed(Location location, String address);
    }




}
