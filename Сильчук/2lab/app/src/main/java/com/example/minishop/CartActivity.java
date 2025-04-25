package com.example.minishop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        listViewCart = findViewById(R.id.listViewCart);

        // Получаем выбранные товары из Intent
        ArrayList<MainActivity.Product> selectedProducts = getIntent()
                .getParcelableArrayListExtra("selectedProducts");

        // Логируем количество полученных товаров
        if (selectedProducts == null) {
            Log.e("CartActivity", "Selected products list is null");
            selectedProducts = new ArrayList<>();
        } else {
            Log.d("CartActivity", "Received products count: " + selectedProducts.size());
        }

        // Отображаем товары в ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        for (MainActivity.Product product : selectedProducts) {
            adapter.add(product.getName() + " - $" + product.getPrice());
        }
        listViewCart.setAdapter(adapter);
    }
}