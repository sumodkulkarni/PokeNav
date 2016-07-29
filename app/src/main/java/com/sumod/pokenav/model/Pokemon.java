package com.sumod.pokenav.model;


import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

import lombok.Data;


@Data
@ParseClassName("Pokemons")
public class Pokemon extends ParseObject {
    Integer getNDex() {
        return (Integer) get("ndex");
    }


    String getName() {
        return (String) get("name");
    }
}
