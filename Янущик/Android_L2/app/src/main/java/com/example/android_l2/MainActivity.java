package com.example.android_l2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button showItemsBtn;
    TextView itemCount;
    ArrayList<Product> products = new ArrayList<>();
    ArrayList<Product> cart = new ArrayList<>();
    CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        // Добавление Header
        View header = LayoutInflater.from(this).inflate(R.layout.list_header, null);
        listView.addHeaderView(header);

        // Добавление Footer
        View footer = LayoutInflater.from(this).inflate(R.layout.list_footer, null);
        itemCount = footer.findViewById(R.id.itemCount);
        showItemsBtn = footer.findViewById(R.id.showItemsBtn);
        listView.addFooterView(footer);

        // Заполнение списка товарами (с картинками)
        for (int i = 1; i <= 25; i++) {
            products.add(new Product(i, "My good №" + i, 100 + i, R.drawable.sample_image));
        }

        adapter = new CustomAdapter(this, products, cart, itemCount);
        listView.setAdapter(adapter);

        showItemsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putParcelableArrayListExtra("cart", cart);
            startActivity(intent);
        });
    }
}