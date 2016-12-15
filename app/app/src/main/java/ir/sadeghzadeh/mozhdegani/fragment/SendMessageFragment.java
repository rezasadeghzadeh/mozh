package ir.sadeghzadeh.mozhdegani.fragment;

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

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.entity.RequestResponse;
import ir.sadeghzadeh.mozhdegani.volley.GsonRequest;

/**
 * Created by reza on 12/15/16.
 */
public class SendMessageFragment extends BaseFragment{
    public static final String TAG = SendMessageFragment.class.getName();
    EditText message;
    Button sendMessage;
    String itemId;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = layoutInflater.inflate(R.layout.send_message, container, false);
        initBackButton();
        initMessage(view);
        initSendMessage(view);

        Bundle args  = getArguments();
        if(args != null &&  !args.getString(Const.ID).isEmpty()){
            itemId = args.getString(Const.ID);
        }
        return view;
    }

    private void initMessage(View view) {
        message  = (EditText) view.findViewById(R.id.message);
    }

    private void initSendMessage(View view) {
        sendMessage = (Button) view.findViewById(R.id.send_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> param = new HashMap<>();
                param.put(Const.ID, itemId);
                param.put(Const.BODY,message.getText().toString());
                GsonRequest<RequestResponse> request = new GsonRequest<RequestResponse>(Const.SEND_MESSAGE_URL, RequestResponse.class, param, null, new Response.Listener<RequestResponse>() {
                    @Override
                    public void onResponse(RequestResponse response) {
                        if(response.Status == 1){
                            Bundle args  = new Bundle();
                            args.putString(Const.ID,itemId);
                            DetailItemFragment  fragment = new DetailItemFragment();
                            fragment.setArguments(args);
                            activity.addFragmentToContainer(fragment,DetailItemFragment.TAG);
                            Toast.makeText(getContext(),getString(R.string.message_sent_successfully),Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),getString(R.string.connection_error),Toast.LENGTH_LONG).show();
                    }
                });
                ApplicationController.getInstance().addToRequestQueue(request);
            }
        });
    }

    private void initBackButton() {
        activity.backButton.setVisibility(View.VISIBLE);
    }

}
