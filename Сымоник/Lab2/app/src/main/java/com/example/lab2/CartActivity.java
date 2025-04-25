package com.example.lab2;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private ListView cartListView;
    private CartAdapter cartAdapter;
    private List<Product> cartProducts = new ArrayList<>();

    static public boolean isOrdered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ListView cartListView = findViewById(R.id.cartListView);
        TextView totalPriceText = findViewById(R.id.totalPriceText);
        Button checkoutButton = findViewById(R.id.checkoutButton);

        cartProducts = (ArrayList<Product>) getIntent().getSerializableExtra("selectedProducts");

        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        cartAdapter = new CartAdapter(this, cartProducts);
        cartListView.setAdapter(cartAdapter);

        double totalPrice = 0;
        for (Product p : cartProducts) {
            totalPrice += p.getPrice() * p.getQuantity();
        }
        totalPriceText.setText("Итого: " + String.format("%.2f", totalPrice) + " руб.");

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartProducts.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Корзина пуста!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Product product : cartProducts) {
                    product.setQuantity(0);
                }
                cartAdapter.notifyDataSetChanged();

                Toast.makeText(CartActivity.this, "Заказ оформлен!", Toast.LENGTH_SHORT).show();

                isOrdered = true;
                finish();
            }
        });
    }

    static boolean getIsOrdered()
    {
        return isOrdered;
    }

    static void setIsOrdered(boolean flag)
    {
        isOrdered = flag;
    }


}
