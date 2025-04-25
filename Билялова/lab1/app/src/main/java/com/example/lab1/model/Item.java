package com.example.lab1.model;

import com.google.gson.annotations.SerializedName;

public class Item {
    private String id;
    private String name;
    private String description;

    @SerializedName("reference_image_id")
    private String referenceImageId;

    public String getTitle() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Возвращаем URL изображения на основе reference_image_id
    public String getImageUrl() {
        return "https://cdn2.thecatapi.com/images/" + referenceImageId + ".jpg";
    }
}
