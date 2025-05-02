package com.example.lab2.headphones_shop;

import java.io.Serializable;

public class Headphones implements Serializable {
    private String name;
    private double price;
    private int imageResId;
    private String description;

    public Headphones(String name, double price, int imageResId, String description) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}
