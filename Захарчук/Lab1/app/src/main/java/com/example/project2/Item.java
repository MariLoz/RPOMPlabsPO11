package com.example.project2;

public class Item {
    private int id;
    private String title;
    private String description;
    private String imageUrl; // Новое поле для URL изображения

    public Item() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String toCsv() {
        return id + "," + title + "," + description + "," + (imageUrl != null ? imageUrl : "");
    }
}