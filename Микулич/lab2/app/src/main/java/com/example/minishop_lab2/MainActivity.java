package com.example.minishop_lab2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private ListView productsListView;
    private ProductAdapter adapter;
    private List<Product> productList;

    private TextView selectedTextView; // Используем существующий TextView из footer
    private static final String JSON_URL = "https://raw.githubusercontent.com/Perhewz-Hellcat/android-lab/refs/heads/main/ign12.json";

    private final ActivityResultLauncher<Intent> cartActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    List<Product> updatedCart = (List<Product>) data.getSerializableExtra("updated_cart");

                    if (updatedCart != null) {
                        // Обновляем информацию о выбранных товарах в списке
                        for (Product product : productList) {
                            boolean isInCart = false;
                            for (Product cartProduct : updatedCart) {
                                if (cartProduct.getName().equals(product.getName())) {
                                    isInCart = true;
                                    break;
                                }
                            }
                            product.setChecked(isInCart); // Обновляем статус чекбокса товара
                        }
                        adapter.notifyDataSetChanged(); // Обновляем список
                    }
                }
                // Пересчитываем количество выбранных товаров
                updateSelectedCount();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_main);

        productsListView = findViewById(R.id.productsListView);
        selectedTextView = findViewById(R.id.selectedTextView);
        productList = new ArrayList<>();

        loadProductsFromUrl();


        // Подключаем адаптер
        adapter = new ProductAdapter(this, productList, count ->
                selectedTextView.setText("Выбрано: " + count));


        productsListView.setAdapter(adapter);
        updateSelectedCount();

        Button openBasketButton = findViewById(R.id.openBasketButton);
        openBasketButton.setOnClickListener(v -> {
            ArrayList<Product> selectedProducts = new ArrayList<>();
            for (Product product : productList) {
                if (product.isChecked()) {  // Только если товар выбран
                    selectedProducts.add(product);
                }
            }

            if (selectedProducts.isEmpty()) {
                Toast.makeText(this, "Выберите товары перед добавлением в корзину!", Toast.LENGTH_SHORT).show();
                return;
            }


            for (Product product : productList) {
                Log.d("MainActivity", "Товар: " + product.getName() + ", Количество: " + product.getQuantity());
            }
            Log.d("MainActivity", "Товары в корзине: " + selectedProducts.size());

            // Передаем только выбранные товары в CartActivity
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            intent.putExtra("cart_products", selectedProducts);
            cartActivityLauncher.launch(intent);
        });



    }
    private void loadProductsFromUrl() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(JSON_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("MainActivity", "Ошибка загрузки JSON: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();

                    new Handler(Looper.getMainLooper()).post(() -> {
                        parseJson(jsonData);
                        adapter.notifyDataSetChanged();

                    });
                }
            }
        });
    }

    private void parseJson(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            Log.d("MainActivity", "Всего элементов: " + jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    productList.add(new Product(
                            jsonObject.getString("imageUrl"),
                            jsonObject.getString("name"),
                            jsonObject.getString("description"),
                            jsonObject.getString("power"),
                            jsonObject.getString("size"),
                            jsonObject.getDouble("price"),
                            0
                    ));
                } catch (Exception e) {
                    Log.e("MainActivity", "Ошибка парсинга элемента " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Ошибка парсинга JSON: " + e.getMessage());
        }
        Toast.makeText(this, "JSON файл успешно загружен!", Toast.LENGTH_SHORT).show();
    }
    public void updateSelectedCount() {
        int selectedCount = 0;
        for (Product product : productList) {
            if (product.isChecked()) {  // Здесь проверяем состояние чекбокса, а не quantity
                selectedCount++;
            }
        }
        selectedTextView.setText("Выбрано: " + selectedCount);
    }
}
