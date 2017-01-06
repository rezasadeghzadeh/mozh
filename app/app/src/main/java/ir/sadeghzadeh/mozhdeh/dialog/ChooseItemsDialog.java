package ir.sadeghzadeh.mozhdeh.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
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
public class ChooseItemsDialog extends DialogFragment implements OnOneItemSelectedInDialog {
    MainActivity activity;
    OnOneItemSelectedInDialog callback;
    List<KeyValuePair> items;
    ListView itemListView;
    boolean selectMultiple;
    List<KeyValuePair> selectedItems;

    public ChooseItemsDialog() {
        activity = (MainActivity) getActivity();
    }

    public void  setArguments(List<KeyValuePair> items, boolean selectMultiple, OnOneItemSelectedInDialog callback  ) {
        this.items  =  items;
        this.callback = callback;
        this.selectMultiple  = selectMultiple;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view  = inflater.inflate(R.layout.choose_one_item_dialog, null);
        builder.setView(view)
                // Add action buttons
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                callback.onItemSelected(selectedItems);
                ChooseItemsDialog.this.dismiss();
            }
        })
        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ChooseItemsDialog.this.dismiss();
            }
        });

        itemListView = (ListView) view.findViewById(R.id.items_list_view);
        itemListView.setAdapter(new KeyValuePairsAdapter(getActivity().getApplicationContext(),0,items,selectMultiple,this));

        return builder.create();
    }

    @Override
    public void onItemSelected(List<KeyValuePair> selected) {
        this.selectedItems  =  selected;
    }
}
