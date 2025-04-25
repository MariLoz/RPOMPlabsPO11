package com.example.lab2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnQuantityChangeListener {
    private ListView cartListView;
    private CartAdapter cartAdapter;
    private List<Product> checkedProducts;
    private Button backButton;
    private List<Integer> removedProductIds;
    private TextView totalPriceTextView;
    private ActivityResultLauncher<Intent> saveFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cartListView = findViewById(R.id.cartListView);
        backButton = findViewById(R.id.backButton);
        removedProductIds = new ArrayList<>();
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        checkedProducts = getIntent().getParcelableArrayListExtra("checkedProducts");
        if (checkedProducts == null) {
            checkedProducts = new ArrayList<>();
        }

        cartAdapter = new CartAdapter(this, checkedProducts);
        cartListView.setAdapter(cartAdapter);

        saveFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                try {
                                    JSONArray jsonArray = convertProductsToJsonArray(checkedProducts);
                                    saveJsonArrayInFile(uri, jsonArray);
                                } catch (IOException | JSONException e) {
                                    Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "URI сохранения отсутствует", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Данные отсутствуют", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Сохранение файла отменено", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cartAdapter.setOnItemRemoveListener(productId -> {
            Iterator<Product> iterator = checkedProducts.iterator();
            while (iterator.hasNext()) {
                Product product = iterator.next();
                if (product.getId() == productId) {
                    iterator.remove();
                    removedProductIds.add(productId);
                    break;
                }
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalSum();
        });

        cartAdapter.setOnQuantityChangeListener(this);

        backButton.setOnClickListener(v -> {
            updateResultIntent();
            setResult(Activity.RESULT_OK, getIntent());
            finish();
        });

        updateTotalSum();
    }

    private JSONArray convertProductsToJsonArray(List<Product> products) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Product product : products) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", product.getId());
            jsonObject.put("name", product.getName());
            jsonObject.put("price", product.getPrice());
            jsonObject.put("imageUrl", product.getImageUrl());
            jsonObject.put("quantity", product.getQuantity());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void exportCheckedProductsToJsonFile(View v) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "checkedProducts.json");
        saveFileLauncher.launch(intent);
    }

    private void saveJsonArrayInFile(Uri uri, JSONArray jsonArray) throws IOException {
        String jsonData = jsonArray.toString();
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                byte[] data = jsonData.getBytes(StandardCharsets.UTF_8);
                outputStream.write(data);
                Toast.makeText(this, "Файл JSON успешно сохранен", Toast.LENGTH_SHORT).show();
                Log.d("FileSaver", "Файл JSON успешно сохранен по URI: " + uri.toString());
            } else {
                Log.e("FileSaver", "Не удалось открыть OutputStream");
                throw new IOException("Не удалось открыть OutputStream");
            }
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        updateResultIntent();
        setResult(Activity.RESULT_OK, getIntent());
        super.onBackPressed();
    }

    private void updateResultIntent() {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("updatedProducts", new ArrayList<>(checkedProducts));
        resultIntent.putIntegerArrayListExtra("removedProductIds", (ArrayList<Integer>) removedProductIds);
        setResult(Activity.RESULT_OK, resultIntent);
        getIntent().putExtras(resultIntent);
    }

    @Override
    public void onQuantityChanged() {
        updateTotalSum();
        updateResultIntent();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateTotalSum() {
        double total = 0.0;
        for (Product product : checkedProducts) {
            total += product.getPrice() * product.getQuantity();
        }
        totalPriceTextView.setText("Итого: " + String.format("%.2f", total) + " BYN");
    }
}
