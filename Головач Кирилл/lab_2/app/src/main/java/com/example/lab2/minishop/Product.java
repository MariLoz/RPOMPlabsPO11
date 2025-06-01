package com.example.lab2.minishop;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private double price;
    private int imageResId;
    private String description;

    public Product(String name, double price, int imageResId, String description) {
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
