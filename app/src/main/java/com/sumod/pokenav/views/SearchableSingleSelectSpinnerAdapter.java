package com.sumod.pokenav.views;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sumod.pokenav.R;

import java.util.ArrayList;
import java.util.List;


public class SearchableSingleSelectSpinnerAdapter extends BaseAdapter implements Filterable {
    List<CheckListItem> arrayList;
    List<CheckListItem> mOriginalValues; // Original Values
    LayoutInflater inflater;
    AlertDialog alertDialog;



    public SearchableSingleSelectSpinnerAdapter(Context context, List<CheckListItem> arrayList) {
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    public SearchableSingleSelectSpinnerAdapter() {
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
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.singleselect_list_item, null);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.singleSelect_item_title);
            holder.textViewDescription = (TextView) convertView.findViewById(R.id.singleSelect_item_description);

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

        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                for (int i = 0; i < arrayList.size(); i++) {
                    if (i == position) {
                        arrayList.get(i).setChecked(true);
                        Log.i("TAG", "On Click Selected : " + arrayList.get(i).getName() + " : " + arrayList.get(i).isChecked());
                    }else {
                        arrayList.get(i).setChecked(false);
                    }
                }

                alertDialog.cancel();
            }
        });

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
