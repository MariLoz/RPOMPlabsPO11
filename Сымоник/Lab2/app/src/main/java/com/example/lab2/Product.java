package com.example.lab2;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private int quantity;

    public Product(int id, String name, double price) {
        this.id = id;
        this.name = (name != null) ? name : "";
        this.price = price;
        this.quantity = 0;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int i) { quantity = i; }

    public void increaseQuantity() { quantity++; }
    public void decreaseQuantity() { if (quantity > 0) quantity--; }
}
