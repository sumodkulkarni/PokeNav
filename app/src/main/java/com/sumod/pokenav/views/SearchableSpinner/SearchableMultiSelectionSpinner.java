package com.sumod.pokenav.views.SearchableSpinner;


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


public class SearchableMultiSelectionSpinner extends Spinner implements OnCancelListener {
    @Getter private List<CheckListItem> checkListItems;
    private String defaultText = "";
    private SearchableMultiSelectionSpinnerListener listener;
    SearchableMultiSelectSpinnerAdapter adapter;


    public SearchableMultiSelectionSpinner(Context context) {
        super(context);
    }


    public SearchableMultiSelectionSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }


    public SearchableMultiSelectionSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }


    public void setSearchableMultiSelectionSpinnerListener(SearchableMultiSelectionSpinnerListener listener) {
        this.listener = listener;
    }


    void updateSpinnerText() {
        StringBuffer spinnerBuffer = new StringBuffer();

        for (int i = 0; i < checkListItems.size(); i++) {
            if (checkListItems.get(i).isChecked()) {
                spinnerBuffer.append(checkListItems.get(i).getName());
                spinnerBuffer.append(", ");
            }
        }

        String spinnerText = "";
        spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2)
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
        else spinnerText = defaultText;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(defaultText);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.multiselect_dialogue, null);
        builder.setView(view);

        final ListView listView = (ListView) view.findViewById(R.id.multiSelect_listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(false);

        adapter = new SearchableMultiSelectSpinnerAdapter(getContext(), checkListItems);
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

//				items = (ArrayList<KeyPairBoolData>) adapter.arrayList;

                        Log.i("TAG", " ITEMS : " + checkListItems.size());
                        dialog.cancel();
                    }
                });

        builder.setOnCancelListener(this);
        builder.show();
        return true;
    }


    public void setItems(List<CheckListItem> items, String allText, int position,
                         SearchableMultiSelectionSpinnerListener listener) {
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


    public interface SearchableMultiSelectionSpinnerListener {
        void onItemsSelected(List<CheckListItem> items);
    }


    public SearchableMultiSelectSpinnerAdapter getAdapter() {
        return adapter;
    }


    public List<Integer> getSelectedItemsId() {
        List<Integer> selectedItemsIds = new ArrayList<>();

        if (checkListItems == null) return new ArrayList<>();

        for (int i = 0; i < checkListItems.size(); i++) {
            if (checkListItems.get(i).isChecked())
                selectedItemsIds.add(checkListItems.get(i).getId());
        }

        return selectedItemsIds;
    }

    public boolean isNothingSelected(){

        if (checkListItems == null) return true;

        for (int i=0; i < checkListItems.size(); i++){
            if (checkListItems.get(i).isChecked()){
                return true;
            }
        }

        return false;
    }
}
























