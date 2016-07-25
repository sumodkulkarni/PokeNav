package com.sumod.pokenav.model;


import java.util.List;

import lombok.Data;


@Data
public class PokemonHistory {
    String createdAt;
    Integer pokemonId;
    List<Integer> location;


    // TODO: Implement this
    Pokemon getPokemon() {
        return null;
    }


    @Data
    public static class SearchResults {
        List<PokemonHistory> docs;
    }
}
