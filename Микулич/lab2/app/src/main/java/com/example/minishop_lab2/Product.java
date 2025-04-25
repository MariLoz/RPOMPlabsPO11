package com.example.minishop_lab2;

import java.io.Serializable;

public class Product implements Serializable {
    private String imageUrl;
    private String name;
    private String description;
    private String power;
    private String size;
    private double price;
    private boolean isChecked;
    private int quantity;

    public Product(String imageUrl, String name, String description, String power, String size, double price, int quantity) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.description = description;
        this.power = power;
        this.size = size;
        this.price = price;
        this.isChecked = false;
        this.quantity = quantity;
    }

    public String getImageUrl() { return imageUrl; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPower() { return power; }
    public String getSize() { return size; }
    public double getPrice() { return price; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
