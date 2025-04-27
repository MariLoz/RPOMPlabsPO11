package com.example.firstlab;

public class Item {
    private int id;
    private String title;
    private String shortDescription;
    private String description;
    private String imageUrl;

    public Item(int id, String title, String shortDescription, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getShortDescription() { return shortDescription; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}