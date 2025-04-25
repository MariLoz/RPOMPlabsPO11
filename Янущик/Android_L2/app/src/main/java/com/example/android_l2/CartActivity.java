package com.example.android_l2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    ListView cartListView;
    CustomAdapter adapter;
    ArrayList<Product> cart;
    TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = findViewById(R.id.cartListView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        cart = getIntent().getParcelableArrayListExtra("cart");

        if (cart == null) {
            cart = new ArrayList<>();  // Проверка на null и инициализация пустого списка
        }

        // Используем только cart для отображения в CartActivity
        adapter = new CustomAdapter(this, cart, new ArrayList<>(), null);
        cartListView.setAdapter(adapter);

        // Обновляем общую цену
        updateTotalPrice();

        // Обработка нажатия на кнопку "Back to List"
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());  // Закрываем CartActivity и возвращаемся к MainActivity
    }

    // Метод для обновления общей цены
    void updateTotalPrice() {
        double totalPrice = 0;
        for (Product product : cart) {
            totalPrice += product.price;
        }
        totalPriceTextView.setText("Total Price: $" + totalPrice);
    }
}