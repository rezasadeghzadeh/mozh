package ir.sadeghzadeh.mozhdeh.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import ir.sadeghzadeh.mozhdeh.ApplicationController;
import ir.sadeghzadeh.mozhdeh.Const;
import ir.sadeghzadeh.mozhdeh.MainActivity;
import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.dialog.MessagesListDialog;
import ir.sadeghzadeh.mozhdeh.entity.Item;
import ir.sadeghzadeh.mozhdeh.fragment.DetailItemFragment;
import ir.sadeghzadeh.mozhdeh.fragment.NewFragment;
import ir.sadeghzadeh.mozhdeh.utils.Util;

/**
 * Created by reza on 11/3/16.
 */
public class ItemsAdapter extends ArrayAdapter<Item>{
    Item[] items;
    private static LayoutInflater inflater = null;
    Context context;
    boolean myItemsMode;
    MainActivity activity;
    public ItemsAdapter(Context context, int resource, Item[] items, MainActivity activity, boolean myItemsMode) {
        super(context, resource, items);
        this.items  =  items;
        this.myItemsMode  =  myItemsMode;
        this.context = context;
        this.activity  =  activity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (items == null) {
            return 0;
        }
        return items.length;
    }

    public Item getItem(int index) {
        return items[index];
    }

    public View getView(int position, View paramView, ViewGroup paramViewGroup) {
        Holder holder = new Holder();
        final Item item = items[position];
        View rowView=null;
        if(paramView == null){
            rowView = inflater.inflate(R.layout.item_row_item, null);
        }else {
            rowView = paramView;
        }
        holder.title = (TextView) rowView.findViewById(R.id.item_title);
        holder.founded= (TextView) rowView.findViewById(R.id.founded_type);
        holder.lost= (TextView) rowView.findViewById(R.id.lost_type);
        //holder.mobile= (TextView) rowView.findViewById(R.id.mobile);
        //holder.category = (TextView) rowView.findViewById(R.id.category);
        //holder.description = (TextView) rowView.findViewById(R.id.description);
        holder.date = (TextView) rowView.findViewById(R.id.date);
        holder.city = (TextView) rowView.findViewById(R.id.city);
        holder.thumbnail = (NetworkImageView) rowView.findViewById(R.id.thumbnail);
        holder.itemContainer = rowView.findViewById(R.id.item_container);
        holder.myItemsButtonContainer  = rowView.findViewById(R.id.my_item_button_container);

        if(myItemsMode){
            holder.myItemsButtonContainer.setVisibility(View.VISIBLE);
            holder.edit  = (Button) rowView.findViewById(R.id.edit);
            holder.preview  = (Button) rowView.findViewById(R.id.preview);
            holder.messages = (Button) rowView.findViewById(R.id.message);

            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args  =  new Bundle();
                    args.putString(Const.ID,item.id);
                    NewFragment fragment  = new NewFragment();
                    fragment.setArguments(args);
                    activity.addFragmentToContainer(fragment,DetailItemFragment.TAG);
                }
            });

            holder.preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args  =  new Bundle();
                    args.putString(Const.ID,item.id);
                    DetailItemFragment fragment  = new DetailItemFragment();
                    fragment.setArguments(args);
                    activity.addFragmentToContainer(fragment,DetailItemFragment.TAG);
                }
            });

            holder.messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(item.Messages != null && item.Messages.size() > 0){
                        MessagesListDialog dialog = new MessagesListDialog();
                        dialog.itemMessages = item.Messages;
                        dialog.show(activity.getSupportFragmentManager(), MessagesListDialog.TAG);
                    }else {
                        Toast.makeText(getContext(),getContext().getString(R.string.no_message_registered),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else {
            holder.myItemsButtonContainer.setVisibility(View.GONE);
        }

        //set values
        holder.title.setText(item.Title);
        //holder.mobile.setText(item.Mobile);
        //holder.category.setText(String.valueOf(item.CategoryId));
        //holder.description.setText(item.Description);
        holder.date.setText(item.Date + "");
        holder.city.setText(String.valueOf(item.CityTitle));
        if(item.ImageExt != null && !item.ImageExt.isEmpty()){
            String uri = Util.imageUrlMaker(true,item);
            holder.thumbnail.setImageUrl(uri, ApplicationController.getInstance().getImageLoaderInstance());
        }else {
            holder.thumbnail.setImageUrl(null, null);
            holder.thumbnail.setDefaultImageResId(R.drawable.ic_no_photo);
        }
        if(item.ItemType.equals(Const.FOUND+"")){
            holder.founded.setVisibility(View.VISIBLE);
            holder.lost.setVisibility(View.GONE);
            holder.itemContainer.setBackgroundColor( context.getResources().getColor(R.color.foundedItemBackground));
        }else if(item.ItemType.equals(Const.LOST+"")){
            holder.founded.setVisibility(View.GONE);
            holder.lost.setVisibility(View.VISIBLE);
            holder.itemContainer.setBackgroundColor( context.getResources().getColor(R.color.lostItemBackground));
        }
        rowView.setTag(item.id);
        return rowView;
    }

    public class Holder {
        View itemContainer;
        TextView title;
        TextView category;
        TextView description;
        TextView date;
        TextView city;
        NetworkImageView thumbnail;
        TextView founded;
        TextView lost;
        View myItemsButtonContainer;
        Button edit;
        Button preview;
        Button messages;

        public Holder() {
        }
    }





}
