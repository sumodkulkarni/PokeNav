package com.sumod.pokenav.model;


import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import lombok.Data;


@Data
@ParseClassName(PokemonLog.PARSE_CLASSNAME)
public class PokemonLog extends BaseModel {
    public static final String PARSE_CLASSNAME = "PokemonLogs";

    private static final String FIELD_LOCATION = "location";
    private static final String FIELD_DISPLAYNAME = "displayName";
    private static final String FIELD_POKEMON = "pokemon";
    private static final String FIELD_FOUNDBY = "foundBy";


    public String getDisplayName() {
        return getString(FIELD_DISPLAYNAME);
    }


    public void setLocation(double latitude, double longitude) {
        put(FIELD_LOCATION, new ParseGeoPoint(latitude, longitude));
    }


    public ParseGeoPoint getLocation() {
        return getParseGeoPoint(FIELD_LOCATION);
    }


    public void setPokemon(Pokemon pokemon) {
        put(FIELD_POKEMON, pokemon);
    }


    public void setCreator(ParseUser user) {
        put(FIELD_FOUNDBY, user);
    }
}
