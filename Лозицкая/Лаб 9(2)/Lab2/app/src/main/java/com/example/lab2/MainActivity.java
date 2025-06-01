package com.example.lab2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ProductAdapter.OnQuantityChangeListener{
    private ListView productListView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private TextView checkedItemsCountTextView;
    private Button showCheckedItemsButton;
    private String url = "https://raw.githubusercontent.com/Artem646/Temp/refs/heads/main/Product.json";
    private ActivityResultLauncher<Intent> cartActivityLauncher;

    @SuppressLint("MissingInflatedId")
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

        productListView = findViewById(R.id.productListView);
        checkedItemsCountTextView = findViewById(R.id.checkedItemsCountTextView);
        showCheckedItemsButton = findViewById(R.id.showCheckedItemsButton);

        @SuppressLint("InflateParams") View headerView = getLayoutInflater().inflate(R.layout.list_header, null);
        productListView.addHeaderView(headerView);

        @SuppressLint("InflateParams") View footerView = getLayoutInflater().inflate(R.layout.list_footer, null);
        productListView.addFooterView(footerView);

        checkedItemsCountTextView = footerView.findViewById(R.id.checkedItemsCountTextView);
        showCheckedItemsButton = footerView.findViewById(R.id.showCheckedItemsButton);

        loadJsonFileFromUrl(url);

        productAdapter = new ProductAdapter(this, productList, this);
        productListView.setAdapter(productAdapter);

        showCheckedItemsButton.setOnClickListener(v -> {
            showCheckedItems();
        });

        updateCheckedItemsCount();

        cartActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ArrayList<Product> updatedProducts = data.getParcelableArrayListExtra("updatedProducts");
                            ArrayList<Integer> removedProductIds = data.getIntegerArrayListExtra("removedProductIds");

                            if (removedProductIds != null) {
                                for (Integer removedProductId : removedProductIds) {
                                    for (Product product : productList) {
                                        if (product.getId() == removedProductId) {
                                            product.setQuantity(0);
                                            break;
                                        }
                                    }
                                }
                            }

                            if (updatedProducts != null) {
                                for (Product updatedProduct : updatedProducts) {
                                    for (Product product : productList) {
                                        if (product.getId() == updatedProduct.getId()) {
                                            product.setQuantity(updatedProduct.getQuantity());
                                            break;
                                        }
                                    }
                                }
                                productAdapter.notifyDataSetChanged();
                                updateCheckedItemsCount();
                            }
                        }
                    }
                });
    }

    public void loadJsonFileFromUrl(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ERROR", "Ошибка загрузки: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка загрузки!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("ERROR", "Ошибка ответа сервера: " + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка ответа сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                    return;
                }

                assert response.body() != null;
                String jsonData = response.body().string();

                try {
                    JSONArray productsJsonArray = new JSONArray(jsonData);
                    runOnUiThread(() -> { loadProductFromJson(productsJsonArray); });

                } catch (JSONException e) {
                    Log.e("ERROR", "Ошибка парсинга JSON: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка парсинга JSON!", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadProductFromJson(JSONArray productsJsonArray) {
        try {
            for (int i = 0; i < productsJsonArray.length(); i++) {
                JSONObject productObject = productsJsonArray.getJSONObject(i);

                int id = productObject.getInt("id");
                String name = productObject.getString("name");
                double price = productObject.getDouble("price");
                String imageUrl = productObject.getString("imageUrl");

                productList.add(new Product(id, name, price, imageUrl));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
        } finally {
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQuantityChanged() {
        updateCheckedItemsCount();
    }

    @SuppressLint("SetTextI18n")
    public void updateCheckedItemsCount() {
        int count = 0;
        for (Product product : productList) {
            count += product.getQuantity();
        }
        checkedItemsCountTextView.setText("Выбрано товаров: " + count);
    }

    private void showCheckedItems() {
        List<Product> checkedProducts = new ArrayList<>();
        for (Product product : productList) {
            if (product.getQuantity() > 0) {
                checkedProducts.add(product);
            }
        }

        Intent intent = new Intent(this, CartActivity.class);
        intent.putParcelableArrayListExtra("checkedProducts", new ArrayList<>(checkedProducts));
        cartActivityLauncher.launch(intent);
    }
}