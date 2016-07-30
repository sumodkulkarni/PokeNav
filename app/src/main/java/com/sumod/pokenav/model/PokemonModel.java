package com.sumod.pokenav.model;

/**
 * Created by sumodkulkarni on 31/7/16.
 */
public class PokemonModel {

    private int nationalDexNumber;
    private String name;
    private String imageThumbnailUri;
    private int generationNumber;

    public int getNationalDexNumber() {
        return nationalDexNumber;
    }

    public void setNationalDexNumber(int nationalDexNumber) {
        this.nationalDexNumber = nationalDexNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageThumbnailUri() {
        return imageThumbnailUri;
    }

    public void setImageThumbnailUri(String imageThumbnailUri) {
        this.imageThumbnailUri = imageThumbnailUri;
    }
}
