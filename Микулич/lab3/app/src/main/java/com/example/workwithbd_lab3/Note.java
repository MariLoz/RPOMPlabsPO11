package com.example.workwithbd_lab3;

public class Note {
    private int _id;
    private String name;
    private String description;
    private String imageUrl;

    public Note(int _id, String name, String description, String imageUrl) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
