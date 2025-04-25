package com.example.laba1;

import java.io.Serializable;

public class Cryptocurrency implements Serializable {
    private String name;
    private String description;
    private String price;
    private String image;
    private String marketCap;
    public Cryptocurrency(String name, String description, String price,
                          String image, String marketCap) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.marketCap = marketCap;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getImage() { return image; }
    public String getMarketCap() { return marketCap; }
}