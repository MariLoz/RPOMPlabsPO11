package com.example.lab2.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView productListView;
    private TextView selectedCount;
    private Button showCartButton;
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<Product> selectedProducts = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productListView = findViewById(R.id.productListView);
        selectedCount = findViewById(R.id.selectedCount);
        showCartButton = findViewById(R.id.showCartButton);

        // Добавляем товары в список
        products.add(new Product("Коктейль шоколадный", 2, R.drawable.chudo, "Молочный коктейль шоколадное чудо"));
        products.add(new Product("Киндер", 25, R.drawable.kinder, "Набор шоколадных конфет со злаками Kinder Country"));
        products.add(new Product("Колбаса сыровяленая", 6, R.drawable.milanka, "Сыровяленая куриная колбаса"));
        products.add(new Product("Набор суши", 50, R.drawable.sushi, "Набор свежих и запеченых суши"));

        adapter = new ProductAdapter(this, products, selectedProducts);
        productListView.setAdapter(adapter);

        // Кнопка "Перейти в корзину"
        showCartButton.setOnClickListener(v -> {
            Cart cart = Cart.getInstance();
            cart.clearCart(); // Чтобы корзина не дублировалась при повторном добавлении

            for (Product product : selectedProducts) {
                cart.addProduct(product);
            }

            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    // Метод для обновления счетчика выбранных товаров
    public void updateSelectedCount() {
        selectedCount.setText("Выбрано товаров: " + selectedProducts.size());
    }
}
