package com.example.kozinlab2;

import java.io.Serializable;
public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private int imageResId; // ID ресурса изображения
    private int quantity;

    public Product(int id, String name, double price,int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0;  // Начальное количество товара
        this.imageResId = imageResId;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
    public int getImageResId() { return imageResId; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }




}
