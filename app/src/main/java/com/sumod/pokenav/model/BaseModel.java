package com.sumod.pokenav.model;


import com.parse.ParseObject;


public class BaseModel extends ParseObject {
    String getName() {
        return (String) get("name");
    }
}
