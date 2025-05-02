package com.example.lab2.headphones_shop;

import java.util.ArrayList;

public class Cart {
    private static Cart instance; // Одиночка (Singleton)
    private ArrayList<Headphones> headphones; // Список товаров

    private Cart() {
        headphones = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void addProduct(Headphones product) {
        headphones.add(product);
    }

    public ArrayList<Headphones> getProducts() {
        return headphones;
    }

    public void clearCart() {
        headphones.clear();
    }
}
