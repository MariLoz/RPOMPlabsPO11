package com.example.lab2k;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnCheckedChangeListener {

    private ListView listView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private TextView textViewFooter;
    private Button buttonShowCheckedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        productList = new ArrayList<>();

        // Добавляем тестовые товары
        productList.add(new Product(1, "Компьютерная мышь", 80.99));
        productList.add(new Product(2, "Клавиатура", 70.49));
        productList.add(new Product(3, "Проводные наушники", 29.99));
        productList.add(new Product(4, "Коврик для мыши", 20.00));
        productList.add(new Product(5, "Монитор", 150.75));
        productList.add(new Product(6, "Игровые наушники", 57.19));


        // Создаём адаптер и подключаем его
        adapter = new ProductAdapter(this, productList, this);
        listView.setAdapter(adapter);

        // Добавляем Footer
        addFooterToListView();
    }

    private void addFooterToListView() {
        LayoutInflater inflater = getLayoutInflater();
        View footerView = inflater.inflate(R.layout.footer_layout, listView, false);

        textViewFooter = footerView.findViewById(R.id.textViewFooter);
        buttonShowCheckedItems = footerView.findViewById(R.id.buttonShowCheckedItems);

        buttonShowCheckedItems.setOnClickListener(v -> {
            ArrayList<Product> selectedProducts = new ArrayList<>();
            for (Product product : productList) {
                if (product.isChecked()) {
                    selectedProducts.add(product);
                }
            }

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("selectedProducts", selectedProducts);  // Передаем список товаров
            startActivity(intent);
        });


        listView.addFooterView(footerView);
    }

    @Override
    public void onCheckedChange() {
        int count = 0;
        for (Product product : productList) {
            if (product.isChecked()) {
                count++;
            }
        }
        textViewFooter.setText("Выбрано товаров: " + count);
    }
}
