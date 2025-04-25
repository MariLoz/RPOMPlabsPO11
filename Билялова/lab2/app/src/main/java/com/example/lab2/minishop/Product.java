package com.example.lab2.minishop;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private int price;
    private int imageResId;
    private String description;

    public Product(String name, int price, int imageResId, String description) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getDescription() {
        return description;
    }
}
