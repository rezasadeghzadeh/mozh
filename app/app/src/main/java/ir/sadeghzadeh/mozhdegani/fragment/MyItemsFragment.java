package ir.sadeghzadeh.mozhdegani.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.sadeghzadeh.mozhdegani.MainActivity;
import ir.sadeghzadeh.mozhdegani.R;

/**
 * Created by reza on 11/2/16.
 */
public class MyItemsFragment extends BaseFragment {
    public static final String TAG="MyItemsFragment";

    MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        activity  = (MainActivity) getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        activity.highlightMyItemsIcon();
        View view = layoutInflater.inflate(R.layout.my_items_fragment, container, false);
        return view;
    }

}
