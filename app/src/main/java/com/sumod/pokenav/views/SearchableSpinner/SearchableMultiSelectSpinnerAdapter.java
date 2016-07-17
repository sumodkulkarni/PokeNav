package com.sumod.pokenav.views.SearchableSpinner;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sumod.pokenav.R;

import java.util.ArrayList;
import java.util.List;


public class SearchableMultiSelectSpinnerAdapter extends BaseAdapter implements Filterable {
    List<CheckListItem> arrayList;
    List<CheckListItem> mOriginalValues; // Original Values
    LayoutInflater inflater;


    public SearchableMultiSelectSpinnerAdapter(Context context, List<CheckListItem> arrayList) {
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }


    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        CheckBox checkBox;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.multiselect_list_item, null);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.multiSelect_item_title);
            holder.textViewDescription = (TextView) convertView.findViewById(R.id.multiSelect_item_description);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.multiSelect_item_checkBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CheckListItem data = arrayList.get(position);

        if (data.getDescription() == null) holder.textViewDescription.setVisibility(View.GONE);
        else {
            holder.textViewDescription.setVisibility(View.VISIBLE);
            holder.textViewDescription.setText(data.getDescription());
        }

        holder.textViewTitle.setText(data.getName());
        holder.checkBox.setChecked(data.isChecked());

        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewHolder temp = (ViewHolder) v.getTag();
                temp.checkBox.setChecked(!temp.checkBox.isChecked());

                int len = arrayList.size();
                for (int i = 0; i < len; i++) {
                    if (i == position) {
                        data.setChecked(!data.isChecked());
                        Log.i("TAG", "On Click Selected : " + data.getName() + " : " + data.isChecked());
                        break;
                    }
                }
            }
        });

        holder.checkBox.setTag(holder);

        return convertView;
    }


    @SuppressLint("DefaultLocale")
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                arrayList = (List<CheckListItem>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<CheckListItem> FilteredArrList = new ArrayList<CheckListItem>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<CheckListItem>(arrayList); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        Log.i("TAG", "Filter : " + mOriginalValues.get(i).getName() + " -> " + mOriginalValues.get(i).isChecked());
                        String data = mOriginalValues.get(i).getName();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
