package ir.sadeghzadeh.mozhdegani.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.Const;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.entity.Item;
import ir.sadeghzadeh.mozhdegani.entity.ItemMessage;
import ir.sadeghzadeh.mozhdegani.fragment.DetailItemFragment;
import ir.sadeghzadeh.mozhdegani.fragment.NewFragment;
import ir.sadeghzadeh.mozhdegani.utils.Util;

/**
 * Created by reza on 12/16/16.
 */
public class ItemMessageAdapter extends ArrayAdapter<ItemMessage>{
    ItemMessage[] itemMessages;
    private final LayoutInflater inflater;

    public ItemMessageAdapter(Context context, int resource,ItemMessage[] itemMessages) {
        super(context, resource, itemMessages);
        this.itemMessages = itemMessages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (itemMessages == null) {
            return 0;
        }
        return itemMessages.length;
    }

    public ItemMessage getItem(int index) {
        return itemMessages[index];
    }

    public View getView(int position, View paramView, ViewGroup paramViewGroup) {
        Holder holder = new Holder();
        final ItemMessage itemMessage = itemMessages[position];
        View rowView=null;
        if(paramView == null){
            rowView = inflater.inflate(R.layout.message_row_item, null);
        }else {
            rowView = paramView;
        }
        holder.body= (TextView) rowView.findViewById(R.id.body);
        holder.date= (TextView) rowView.findViewById(R.id.date);
        holder.date.setText(itemMessage.CreateDate + "");
        holder.body.setText(itemMessage.Body);
        rowView.setTag(itemMessage.id);
        return rowView;
    }

    public class Holder {
        TextView body;
        TextView date;


        public Holder() {
        }
    }

}
