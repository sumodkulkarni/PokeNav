package com.sumod.pokenav.model;


import com.parse.ParseClassName;
import com.parse.ParseObject;

import lombok.Data;


@Data
@ParseClassName(Pokemon.PARSE_CLASSNAME)
public class Pokemon extends ParseObject {
    public static final String PARSE_CLASSNAME = "Pokemons";


    Integer getNDex() {
        return (Integer) get("ndex");
    }


    String getName() {
        return (String) get("name");
    }
}
