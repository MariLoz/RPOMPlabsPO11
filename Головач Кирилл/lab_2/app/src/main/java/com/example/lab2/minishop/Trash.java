package com.example.lab2.minishop;

import java.util.ArrayList;

public class Trash {
    private static Trash instance; // Одиночка (Singleton)
    private ArrayList<Product> products; // Список товаров

    private Trash() {
        products = new ArrayList<>();
    }

    public static Trash getInstance() {
        if (instance == null) {
            instance = new Trash();
        }
        return instance;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void clearCart() {
        products.clear();
    }
}
