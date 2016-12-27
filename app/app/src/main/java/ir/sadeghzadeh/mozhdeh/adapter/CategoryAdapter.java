package ir.sadeghzadeh.mozhdeh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ir.sadeghzadeh.mozhdeh.R;
import ir.sadeghzadeh.mozhdeh.entity.Category;

/**
 * Created by reza on 11/4/16.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {
    private final LayoutInflater inflater;
    Category[] categories;
    public CategoryAdapter(Context context, int resource, Category[] categories) {
        super(context, resource, categories);
        this.categories  =  categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        if (categories == null) {
            return 0;
        }
        return categories.length;
    }

    public Category getItem(int index) {
        return categories[index];
    }

    public View getView(int position, View paramView, ViewGroup paramViewGroup) {
        Holder holder = new Holder();
        Category category = categories[position];
        View rowView;
        if(paramView  == null){
            rowView = inflater.inflate(R.layout.category_row, null);
        }else {
            rowView = paramView;
        }
        holder.title = (TextView) rowView.findViewById(R.id.title);
        rowView.setTag(category.Id+ "," + category.Title);
        //set values
        holder.title.setText(category.Title);
        return rowView;
    }

    public class Holder {
        TextView title;
        public Holder() {
        }
    }

}
