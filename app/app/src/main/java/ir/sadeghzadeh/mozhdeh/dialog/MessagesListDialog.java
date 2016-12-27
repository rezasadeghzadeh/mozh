package ir.sadeghzadeh.mozhdeh.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.adapter.ItemMessageAdapter;
import ir.sadeghzadeh.mozhdeh.entity.ItemMessage;

/**
 * Created by reza on 12/16/16.
 */
public class MessagesListDialog  extends DialogFragment {
    public static final String TAG = MessagesListDialog.class.getName();
    MainActivity activity;
    public List<ItemMessage> itemMessages;
    ListView listView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.message_list, null);
        listView = (ListView) view.findViewById(R.id.message_list);
        ItemMessage[] itemMessagesArr  = new ItemMessage[itemMessages.size()];
        itemMessages.toArray(itemMessagesArr);
        listView.setAdapter(new ItemMessageAdapter(getContext(),0,itemMessagesArr));
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dismiss();
                    }
                });
        builder.setTitle(getString(R.string.messages));
        return builder.create();
    }
}
