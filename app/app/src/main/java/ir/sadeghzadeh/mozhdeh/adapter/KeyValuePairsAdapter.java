package ir.sadeghzadeh.mozhdeh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.dialog.OnOneItemSelectedInDialog;
import ir.sadeghzadeh.mozhdeh.entity.KeyValuePair;

/**
 * Created by reza on 11/11/16.
 */
public class KeyValuePairsAdapter extends ArrayAdapter<KeyValuePair> {
    List<KeyValuePair> items;
    LayoutInflater layoutInflater;
    OnOneItemSelectedInDialog callback;
    List<KeyValuePair>  selected = new ArrayList<>();
    boolean selectMultiple;
    public KeyValuePairsAdapter(Context context, int resource, List<KeyValuePair> items, boolean selectMultiple, OnOneItemSelectedInDialog callback) {
        super(context, resource, items);
        this.items  =  items;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback  =  callback;
        this.selectMultiple  =  selectMultiple;
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
                //TODO if selectMultipe is the view should be check box else radiobox
                rowView= layoutInflater.inflate(R.layout.key_value_pair_row_item, null);
            }else {
                rowView = paramView;
            }

            holder.value= (TextView) rowView.findViewById(R.id.value);
            rowView.setTag(item.key+ "," + item.value);
            //set values
            holder.value.setText(item.value);

            holder.checkbox  = (CheckBox) rowView.findViewById(R.id.checkbox);
            holder.checkbox.setTag(item.key+ "," + item.value);
            if(selected.contains(item)){
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }

           /* for (KeyValuePair selectedRow : selected) {
                if (selectedRow.key.equals(item.key)) {
                    holder.checkbox.setChecked(true);
                } else {
                    holder.checkbox.setChecked(false);
                }
            }*/


            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
                    String[] rowValues =  checkbox.getTag().toString().split(",");
                        if(isChecked){
                            selected.add(new KeyValuePair(rowValues[0],rowValues[1]));
                            checkbox.setChecked(true);
                        }else {
                            for(int  i=0;i< selected.size();i++){
                                if(selected.get(i).key.equals(rowValues[0])){
                                    selected.remove(i);
                                    checkbox.setChecked(false);
                                    break;
                                }
                            }
                        }
                        callback.onItemSelected(selected);

                }
            });

            return rowView;

    }

    public class Holder {
        TextView value;
        CheckBox checkbox;
        public Holder() {
        }
    }


}
