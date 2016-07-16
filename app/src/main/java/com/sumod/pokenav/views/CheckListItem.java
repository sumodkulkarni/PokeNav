package com.sumod.pokenav.views;


import lombok.Data;


/**
 * Created by sumodkulkarni on 25/6/16.
 */
@Data
public class CheckListItem {
    private int id;
    private String name;
    private String description;
    private boolean checked = false;


    public CheckListItem(int id, String name, String description, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.checked = isChecked;
    }
}
