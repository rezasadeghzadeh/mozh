package ir.sadeghzadeh.mozhdeh.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;

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

    public void animate(View view){
        YoYo.with(Techniques.Landing)
                .duration(800)
                .playOn(view);
    }
}
