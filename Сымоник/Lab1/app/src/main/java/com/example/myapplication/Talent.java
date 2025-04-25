package com.example.myapplication;

import java.io.Serializable;

public class Talent implements Serializable {
    private String name;
    private String type;
    private String thumbnail;
    private String link;

    public Talent(String name, String thumbnail, String link) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.link = link;
    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
}