package com.example.myapplication;

import java.io.Serializable;
import java.util.List;

public class Character implements Serializable {
    private String name;
    private int rarity;
    private String element;
    private String weapon;
    private String region;
    private List<Talent> talents;
    private List<Constellation> constellations;
    private String thumbnail;
    private String link;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getRarity() { return rarity; }
    public void setRarity(int rarity) { this.rarity = rarity; }

    public String getElement() { return element; }
    public void setElement(String element) { this.element = element; }

    public String getWeapon() { return weapon; }
    public void setWeapon(String weapon) { this.weapon = weapon; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public List<Talent> getTalents() { return talents; }
    public void setTalents(List<Talent> talents) { this.talents = talents; }

    public List<Constellation> getConstellations() { return constellations; }
    public void setConstellations(List<Constellation> constellations) { this.constellations = constellations; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}
