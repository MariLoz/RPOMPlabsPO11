package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private TextView textViewCheckedCount, textViewTotalSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        textViewCheckedCount = findViewById(R.id.textViewCheckedCount);
        textViewTotalSum = findViewById(R.id.textViewTotalSum);
        Button buttonShowCheckedItems = findViewById(R.id.buttonShowCheckedItems);
        EditText editTextProductName = findViewById(R.id.editTextProductName);
        EditText editTextProductPrice = findViewById(R.id.editTextProductPrice);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);

        // Инициализация списка товаров
        productList = new ArrayList<>();
        productList.add(new Product(1, "Banana", 7.0));
        productList.add(new Product(2, "Apple", 4.50));
        productList.add(new Product(3, "Peach", 17.0));
        productList.add(new Product(4, "Butter", 3.20));
        productList.add(new Product(5, "Gum", 1.73));

        productAdapter = new ProductAdapter(this, productList);
        listView.setAdapter(productAdapter);

        // Обновление счетчиков при выборе товара
        productAdapter.setOnCheckboxClickListener(new ProductAdapter.OnCheckboxClickListener() {
            @Override
            public void onCheckboxClick() {
                updateCheckedCountAndSum();
            }
        });

        // Добавление нового товара
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextProductName.getText().toString();
                String priceText = editTextProductPrice.getText().toString();

                if (name.isEmpty() || priceText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price = Double.parseDouble(priceText);
                int id = productList.size() + 1;

                productList.add(new Product(id, name, price));
                productAdapter.notifyDataSetChanged();

                editTextProductName.setText("");
                editTextProductPrice.setText("");
            }
        });

        // Переход к выбранным товарам
        buttonShowCheckedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Product> checkedProducts = new ArrayList<>();
                for (Product product : productList) {
                    if (product.isChecked()) {
                        checkedProducts.add(product);
                    }
                }

                if(checkedProducts.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Товары не добавлены!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putParcelableArrayListExtra("checkedProducts", new ArrayList<>(checkedProducts));
                startActivity(intent);
            }
        });
    }

    // Метод для обновления количества выбранных товаров и суммы
    private void updateCheckedCountAndSum() {
        int count = 0;
        double totalSum = 0;

        for (Product product : productList) {
            if (product.isChecked()) {
                count++;
                totalSum += product.getPrice();
            }
        }

        textViewCheckedCount.setText("Добавлено: " + count);
        textViewTotalSum.setText("Итого: " + totalSum + " BYN");
    }
}
