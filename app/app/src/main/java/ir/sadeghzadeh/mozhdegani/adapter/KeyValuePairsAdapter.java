package ir.sadeghzadeh.mozhdegani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import ir.sadeghzadeh.mozhdegani.ApplicationController;
import ir.sadeghzadeh.mozhdegani.MyR;
import ir.sadeghzadeh.mozhdegani.R;
import ir.sadeghzadeh.mozhdegani.entity.Item;
import ir.sadeghzadeh.mozhdegani.entity.KeyValuePair;

/**
 * Created by reza on 11/11/16.
 */
public class KeyValuePairsAdapter extends ArrayAdapter<KeyValuePair> {
    List<KeyValuePair> items;
    LayoutInflater layoutInflater;

    public KeyValuePairsAdapter(Context context, int resource, List<KeyValuePair> items) {
        super(context, resource, items);
        this.items  =  items;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public KeyValuePair getItem(int index) {
        return items.get(index);
    }


    public View getView(int position, View paramView, ViewGroup paramViewGroup) {
        Holder holder = new Holder();
        KeyValuePair item = items.get(position);
        View rowView;
        if( paramView == null){
            rowView= layoutInflater.inflate(R.layout.key_value_pair_row_item, null);
        }else {
            rowView = paramView;
        }

        holder.value= (TextView) rowView.findViewById(R.id.value);
        rowView.setTag(item.key+ "," + item.value);

        //set values
        holder.value.setText(item.value);
        return rowView;
    }

    public class Holder {
        TextView value;
        public Holder() {
        }
    }


}
