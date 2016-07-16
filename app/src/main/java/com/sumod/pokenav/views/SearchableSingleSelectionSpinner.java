package com.sumod.pokenav.views;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.sumod.pokenav.R;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


public class SearchableSingleSelectionSpinner extends Spinner implements OnCancelListener {
    @Getter private List<CheckListItem> checkListItems = new ArrayList<>();
    private String defaultText = "";
    private SearchableSingleSelectionSpinnerListener listener;
    SearchableSingleSelectSpinnerAdapter adapter;
    private AlertDialog.Builder builder;
    private OnCancelListener onCancelListener;


    public SearchableSingleSelectionSpinner(Context context) {
        super(context);
    }


    public SearchableSingleSelectionSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }


    public SearchableSingleSelectionSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }


    public void setSearchableSingleSelectionSpinnerListener(SearchableSingleSelectionSpinnerListener listener) {
        this.listener = listener;
    }


    void updateSpinnerText() {
        String spinnerText = defaultText;

        if (this.adapter != null) {
            for (int i = 0; i < this.adapter.arrayList.size(); i++) {
                if (this.adapter.arrayList.get(i).isChecked()) {
                    spinnerText = this.adapter.arrayList.get(i).getName();
                    break;
                }
            }
        }

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getContext(),
                R.layout.multiselect_spinner_textview,
                new String[]{spinnerText});
        setAdapter(adapterSpinner);
    }


    public void updateSpinner() {
        if (listener != null) listener.onItemsSelected(checkListItems);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        updateSpinnerText();

        if (adapter != null) adapter.notifyDataSetChanged();
        if (listener != null) listener.onItemsSelected(checkListItems);
    }


    @Override
    public boolean performClick() {
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(defaultText);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.multiselect_dialogue, null);
        builder.setView(view);

        final ListView listView = (ListView) view.findViewById(R.id.multiSelect_listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setFastScrollEnabled(false);

        adapter = new SearchableSingleSelectSpinnerAdapter(getContext(), checkListItems);
        listView.setAdapter(adapter);

        EditText editText = (EditText) view.findViewById(R.id.ed_multiSelect_filter);
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

        //builder.setMultiChoiceItems(items.toArray(new CharSequence[items.size()]), selected, this);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("TAG", " ITEMS : " + checkListItems.size());
                        dialog.cancel();
                    }
                });

        builder.setOnCancelListener(this);
        this.adapter.alertDialog = builder.show();
        return true;
    }


    public void setItems(List<CheckListItem> items, String allText, int position,
                         SearchableSingleSelectionSpinnerListener listener) {
        this.checkListItems = items;
        this.defaultText = allText;
        this.listener = listener;

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getContext(),
                R.layout.multiselect_spinner_textview,
                new String[]{defaultText});
        setAdapter(adapterSpinner);

        if (position != -1) {
            items.get(position).setChecked(true);
            onCancel(null);
        }

        updateSpinnerText();
    }


    public interface SearchableSingleSelectionSpinnerListener {
        void onItemsSelected(List<CheckListItem> items);
    }


    public SearchableSingleSelectSpinnerAdapter getAdapter() {
        return adapter;
    }


    public CheckListItem getSelectedItem() {
        for (int i = 0; i < checkListItems.size(); i++) {
            if (checkListItems.get(i).isChecked()) return checkListItems.get(i);
        }
        return null;
    }
}
























