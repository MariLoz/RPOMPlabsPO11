package com.example.lab2.minishop;

import java.util.ArrayList;

public class Cart {
    private static Cart instance; // Одиночка (Singleton)
    private ArrayList<Product> products; // Список товаров

    private Cart() {
        products = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
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
