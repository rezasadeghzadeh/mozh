package ir.sadeghzadeh.mozhdeh.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.adapter.KeyValuePairsAdapter;
import ir.sadeghzadeh.mozhdeh.entity.KeyValuePair;

/**
 * Created by reza on 11/11/16.
 */
public class ChooseOneItemDialog extends DialogFragment {
    MainActivity activity;
    OnOneItemSelectedInDialog callback;
    List<KeyValuePair> items;
    ListView itemListView;

    public ChooseOneItemDialog() {
        activity = (MainActivity) getActivity();
    }

    public void  setArguments(List<KeyValuePair> items, OnOneItemSelectedInDialog callback  ) {
        this.items  =  items;
        this.callback = callback;

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view  = inflater.inflate(R.layout.choose_one_item_dialog, null);
        builder.setView(view);
       /* builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChooseOneItemDialog.this.dismiss();
                    }
                });*/
        itemListView = (ListView) view.findViewById(R.id.items_list_view);
        itemListView.setAdapter(new KeyValuePairsAdapter(getActivity().getApplicationContext(),0,items));
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] rowValues =  view.getTag().toString().split(",");
                callback.onItemSelected(rowValues[0],rowValues[1]);
                ChooseOneItemDialog.this.dismiss();
            }
        });
        return builder.create();
    }

}
