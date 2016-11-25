package ir.sadeghzadeh.mozhdegani.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ir.sadeghzadeh.mozhdegani.MainActivity;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.entity.CurrentLocation;
import ir.sadeghzadeh.mozhdegani.utils.Util;

/**
 * Created by reza on 11/25/16.
 */
public class ChooseLocationOnMapDialog extends DialogFragment implements OnMapReadyCallback{

    MainActivity activity;
    private GoogleMap mMap;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view  = inflater.inflate(R.layout.chose_location_on_map_dialog, null);

        SupportMapFragment mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        mapFragment.getMapAsync(this);
        Util.getLocation();

        builder.setView(view);
       builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChooseLocationOnMapDialog.this.dismiss();
                    }
                });
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CurrentLocation  location= Util.getLocation();
        // Add a marker in Sydney and move the camera
        if(location == null){
            showSettingsAlert();
        }
        location= Util.getLocation();
        if(location != null){
            LatLng currentLatLng = new LatLng(location.latitude,location.longitude);
            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
        }

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


}
