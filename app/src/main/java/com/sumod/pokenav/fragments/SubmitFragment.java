package com.sumod.pokenav.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.sumod.pokenav.R;
import com.sumod.pokenav.adapter.PokemonListAdapter;
import com.sumod.pokenav.model.PokemonModel;
import com.sumod.pokenav.views.SearchableSpinner.CheckListItem;
import com.sumod.pokenav.views.SearchableSpinner.SearchableSingleSelectSpinnerAdapter;
import com.sumod.pokenav.views.SearchableSpinner.SearchableSingleSelectionSpinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@EFragment(R.layout.fragment_submit)
public class SubmitFragment extends Fragment {

    @ViewById(R.id.multiSelect_listView) ListView listView;
    @ViewById(R.id.ed_multiSelect_filter) EditText editText;

    @Getter
    private List<PokemonModel> pokemonModels = new ArrayList<>();
    PokemonListAdapter adapter;
    private SearchableSingleSelectionSpinner.SearchableSingleSelectionSpinnerListener listener;

    public SubmitFragment() {
        // Required empty public constructor
    }


    @AfterViews
    protected void afterViews(){

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFastScrollEnabled(false);

        adapter = new PokemonListAdapter(getContext(), pokemonModels);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


}
