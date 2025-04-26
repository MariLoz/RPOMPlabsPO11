package com.example.lab2k;

import android.os.Bundle;
import android.widget.ListView;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;


import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;  // Используем правильное имя
    private ProductAdapter adapter;
    private ArrayList<Product> selectedProducts;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Находим listViewCart вместо cartListView
        listViewCart = findViewById(R.id.listViewCart);
        buttonBack = findViewById(R.id.buttonBack);  // Находим кнопку

        // Получаем список выбранных товаров
        selectedProducts = (ArrayList<Product>) getIntent().getSerializableExtra("selectedProducts");

        // Проверяем, не пустой ли список
        if (selectedProducts != null && !selectedProducts.isEmpty()) {
            adapter = new ProductAdapter(this, selectedProducts, null);
            listViewCart.setAdapter(adapter);
        } else {
            // Если список пуст, показываем сообщение
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
        }

        // Обработчик нажатия кнопки "Назад"
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // Закрываем CartActivity и возвращаемся назад
            }
        });
    }
}

