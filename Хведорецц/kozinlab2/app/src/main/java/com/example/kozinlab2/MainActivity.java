package com.example.kozinlab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private ListView listView;
    private TextView totalQuantityTextView;
    private Button buttonShowCheckedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        totalQuantityTextView = findViewById(R.id.textViewTotalQuantity);
        buttonShowCheckedItems = findViewById(R.id.buttonShowCheckedItems);

        // Инициализация списка товаров
        productList.add(new Product(1, "Liker", 1.99, R.drawable.liker));
        productList.add(new Product(2, "Pivo", 0.99, R.drawable.pivo));
        productList.add(new Product(3, "Shampanskoe", 1.49, R.drawable.shampun));
        productList.add(new Product(4, "Vino", 2.52, R.drawable.vino));
        productList.add(new Product(5, "Viski", 5.66, R.drawable.viski));
        productList.add(new Product(6, "Vodka", 3.49, R.drawable.vodka));
        productList.add(new Product(7, "Rom", 5.55, R.drawable.rom));
        productList.add(new Product(8, "Jin", 8.11, R.drawable.jin));
        productList.add(new Product(9, "Brendi", 11.49, R.drawable.brendi));
        productList.add(new Product(10, "Sidr", 2.49, R.drawable.sidr));
        productList.add(new Product(10, "Konyak", 55.49, R.drawable.konyak));

        // Инициализация адаптера
        productAdapter = new ProductAdapter(this, productList, false);
        listView.setAdapter(productAdapter);

        // Кнопка для перехода в корзину
        buttonShowCheckedItems.setOnClickListener(v -> {
            ArrayList<Product> selectedItems = new ArrayList<>();
            for (Product product : productList) {
                if (product.getQuantity() > 0) { // Добавляем только выбранные товары
                    selectedItems.add(product);
                }
            }
            int totalQuantity = productAdapter.getTotalQuantity();
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("selectedItems", selectedItems); // Передаем товары в корзину
            intent.putExtra("totalQuantity", totalQuantity); // Передаем количество товаров
            startActivity(intent);
        });
    }

    // Метод для обновления общего количества товаров
    public void updateTotalQuantity(int totalQuantity) {
        totalQuantityTextView.setText("Товары: " + totalQuantity);
    }
}
