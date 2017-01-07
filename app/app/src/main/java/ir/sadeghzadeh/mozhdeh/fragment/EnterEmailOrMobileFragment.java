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
import ir.sadeghzadeh.mozhdeh.entity.RequestResponse;
import ir.sadeghzadeh.mozhdeh.utils.Util;
import ir.sadeghzadeh.mozhdeh.volley.GsonRequest;

/**
 * Created by reza on 12/10/16.
 */
public class EnterEmailOrMobileFragment extends BaseFragment {
    public static final String TAG = EnterEmailOrMobileFragment.class.getName();
    Button next;
    EditText email;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.enter_email_mobile_fragment, container, false);
        initEmail(view);
        initContinue(view);
        animate(view.findViewById(R.id.main_layout));

        return view;
    }

    private void initEmail(View view) {
        email = (EditText) view.findViewById(R.id.email);
    }

    private void initContinue(View view) {
        next = (Button) view.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check email address
                if (email.getText().toString().trim().equals("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setError(getString(R.string.invalid_email));
                    email.requestFocus();
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                activity.showProgress();
                GsonRequest<RequestResponse> request = new GsonRequest<RequestResponse>(Const.SEND_PASS_TO_EMAIL_URL, RequestResponse.class, params, null, new Response.Listener<RequestResponse>() {
                    @Override
                    public void onResponse(RequestResponse response) {
                        if (response.Status == 1) {
                            Util.saveInPreferences(Const.USERNAME, email.getText().toString());
                            activity.hideProgress();
                            EnterPasswordFragment fragment = new EnterPasswordFragment();
                            activity.addFragmentToContainer(fragment, EnterPasswordFragment.TAG);
                        } else {
                            activity.hideProgress();
                            Toast.makeText(getContext(), response.Message, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), getString(R.string.connection_error), Toast.LENGTH_LONG).show();
                    }
                });
                ApplicationController.getInstance().addToRequestQueue(request);
            }
        });

    }

}
