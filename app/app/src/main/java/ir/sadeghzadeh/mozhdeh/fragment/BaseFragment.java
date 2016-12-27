package ir.sadeghzadeh.mozhdeh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import ir.sadeghzadeh.mozhdeh.MainActivity;

/**
 * Created by reza on 11/1/16.
 */
public class BaseFragment extends Fragment {
    MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

}
