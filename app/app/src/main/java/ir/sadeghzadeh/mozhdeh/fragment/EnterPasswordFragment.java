package ir.sadeghzadeh.mozhdeh.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.entity.AuthResponse;
import ir.sadeghzadeh.mozhdeh.utils.Util;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;

/**
 * Created by reza on 12/10/16.
 */
public class EnterPasswordFragment extends BaseFragment {

    public static final String TAG = EnterPasswordFragment.class.getName();
    EditText password;
    Button next;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.enter_password_fragment, container, false);
        initPassword(view);
        initNext(view);
        animate(view.findViewById(R.id.main_layout));

        return view;
    }

    private void initNext(View view) {
        next = (Button) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().trim().equals("")) {
                    password.setError(getString(R.string.invalid_password));
                    password.requestFocus();
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put(Const.PASSWORD, password.getText().toString());
                params.put(Const.USERNAME, Util.fetchFromPreferences(Const.USERNAME));
                params.put(Const.FIREBASE_TOKEN, Util.fetchFromPreferences(Const.FIREBASE_TOKEN));

                activity.showProgress();
                GsonRequest<AuthResponse> request = new GsonRequest<AuthResponse>(Const.AUTH_USER_URL, AuthResponse.class, params, null, new Response.Listener<AuthResponse>() {
                    @Override
                    public void onResponse(AuthResponse response) {
                        if (response.Status == 1) {
                            Util.saveInPreferences(Const.TOKEN, response.Token);
                            activity.hideProgress();
                            NewFragment fragment = new NewFragment();
                            activity.addFragmentToContainer(fragment, NewFragment.TAG);
                        } else if (response.Status == 0) {
                            activity.hideProgress();
                            Toast.makeText(getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        activity.hideProgress();
                        Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    }
                });
                ApplicationController.getInstance().addToRequestQueue(request);

            }
        });
    }

    private void initPassword(View view) {
        password = (EditText) view.findViewById(R.id.password);
    }


}
