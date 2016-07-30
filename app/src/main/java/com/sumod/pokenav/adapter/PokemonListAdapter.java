package com.sumod.pokenav.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumod.pokenav.R;
import com.sumod.pokenav.model.PokemonModel;

import java.util.ArrayList;
import java.util.List;


public class PokemonListAdapter extends BaseAdapter implements Filterable {
    List<PokemonModel> arrayList;
    List<PokemonModel> mOriginalValues; // Original Values
    PokemonModel selectedPokemon;
    LayoutInflater inflater;



    public PokemonListAdapter(Context context, List<PokemonModel> arrayList) {
        this.arrayList = arrayList;
        inflater = LayoutInflater.from(context);
    }

    public PokemonListAdapter() {
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
        TextView pokemonDexNo;
        ImageView pokemonThumbnail;
        TextView pokemonName;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pokemon_list_item, null);
            holder.pokemonDexNo = (TextView) convertView.findViewById(R.id.pokemon_dex_no);
            holder.pokemonThumbnail = (ImageView) convertView.findViewById(R.id.pokemon_thumbnail);
            holder.pokemonName = (TextView) convertView.findViewById(R.id.pokemon_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PokemonModel data = arrayList.get(position);

        holder.pokemonDexNo.setText(data.getNationalDexNumber());
//        holder.pokemonThumbnail.setImageURI(Uri.parse(data.getImageThumbnailUri()));
        holder.pokemonName.setText(data.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectedPokemon = arrayList.get(position);
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

                arrayList = (List<PokemonModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<PokemonModel> FilteredArrList = new ArrayList<PokemonModel>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<PokemonModel>(arrayList); // saves the original data in mOriginalValues
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
                        Log.i("TAG", "Filter : " + mOriginalValues.get(i).getName() + " selected");
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

