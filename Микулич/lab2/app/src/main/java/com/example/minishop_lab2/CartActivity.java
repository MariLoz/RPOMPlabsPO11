package com.example.minishop_lab2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.UpdateTotalPriceListener{

    private ListView cartListView;
    private CartAdapter cartAdapter;
    private TextView totalPriceTextView;
    private List<Product> cartProducts;

    @Override
    public void onUpdate(double totalPrice) {
        totalPriceTextView.setText("К оплате: $" + String.format("%.2f", totalPrice));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = findViewById(R.id.cartListView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Получаем список продуктов из Intent
        cartProducts = (List<Product>) getIntent().getSerializableExtra("cart_products");

        if (cartProducts == null || cartProducts.isEmpty()) {
            Log.e("CartActivity", "Корзина пуста!");
        } else {
            Log.d("CartActivity", "Получено товаров: " + cartProducts.size());
        }

        // Устанавливаем адаптер
        cartAdapter = new CartAdapter(this, cartProducts, this);
        cartListView.setAdapter(cartAdapter);

        updateTotalPrice();

        Button backButton = new Button(this);
        backButton.setText("Назад");
        backButton.setOnClickListener(v -> returnToMainScreen());

        LinearLayout layout = findViewById(R.id.cartLayout);
        layout.addView(backButton);
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (Product product : cartProducts) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        totalPriceTextView.setText("К оплате: $" + String.format("%.2f", totalPrice));
    }

    private void returnToMainScreen() {
        Intent intent = new Intent();
        intent.putExtra("updated_cart", (Serializable) cartProducts);
        setResult(RESULT_OK, intent);
        finish();
    }
}
