package com.sumod.pokenav.model;


import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import lombok.Data;


@Data
@ParseClassName(PokemonLog.PARSE_CLASSNAME)
public class PokemonLog extends ParseObject {
    public static final String PARSE_CLASSNAME = "PokemonLogs";


    public String getDisplayName() {
        return getString("displayName");
    }


    public void setLocation(double latitude, double longitude) {
        put("location", new ParseGeoPoint(latitude, longitude));
    }


    public void setPokemon(Pokemon pokemon) {
        put("pokemon", pokemon);
    }


    public void setCreator(ParseUser user) {
        put("foundBy", user);
    }


    @Data
    public static class SearchResults {
        List<PokemonLog> docs;
    }
}
