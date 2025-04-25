package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class CharacterItem implements Serializable {
    private String name;
    private String element;
    private String weapon;
    private String region;
    private String thumbnail;
    private String wikiUrl;
    private int rarity;

    private ArrayList<Talent> talents;

    private ArrayList<Constellation> constellations;

    public CharacterItem(String name, String element, String weapon, String region, String thumbnail, String wikiUrl, int rarity, ArrayList<Talent> talents, ArrayList<Constellation> constellations) {
        this.name = name;
        this.element = element;
        this.weapon = weapon;
        this.region = region;
        this.thumbnail = thumbnail;
        this.wikiUrl = wikiUrl;
        this.rarity = rarity;
        this.talents = talents;
        this.constellations = constellations;
    }

    public String getName() {
        return name;
    }

    public String getElement() {
        return element;
    }

    public String getWeapon() {
        return weapon;
    }

    public String getRegion() {
        return region;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public int getRarity(){
        return rarity;
    }

    public ArrayList<Talent> getTalents()
    {
        return talents;
    }

    public ArrayList<Constellation> getConstellations()
    {
        return constellations;
    }


}
