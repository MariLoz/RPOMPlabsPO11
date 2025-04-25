package com.example.lab1;

import java.io.Serializable;
public class Phones implements Serializable {
    private String imageUrl;
    private String name;
    private String description;
    private String power;
    private String size;
    private String year;

    public Phones(String imageUrl, String name, String description, String power, String size, String year) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.power = power;
        this.size = size;
        this.year = year;
    }

    public String getImageUrl() { return imageUrl; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPower() { return power; }
    public String getSize() { return size; }
    public String getYear() { return year; }
}
