package com.example.lab2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private List<Product> checkedProducts;
    private TextView textViewCartCount, textViewCartSum;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ListView listViewCart = findViewById(R.id.listViewCart);
        Button buttonBack = findViewById(R.id.buttonBack);
        textViewCartCount = findViewById(R.id.textViewCartCount);
        textViewCartSum = findViewById(R.id.textViewCartSum);

        // Получаем выбранные товары
        checkedProducts = getIntent().getParcelableArrayListExtra("checkedProducts");
        if (checkedProducts == null) {
            checkedProducts = new ArrayList<>();
        }

        // Инициализация адаптера для корзины
        cartAdapter = new CartAdapter(this, checkedProducts);
        listViewCart.setAdapter(cartAdapter);

        // Обновляем текстовые поля с количеством и суммой
        updateCartCountAndSum();

        // Кнопка возврата
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем текущую активность
            }
        });
    }

    // Метод для обновления количества и суммы
    private void updateCartCountAndSum() {
        int count = checkedProducts.size();
        double totalSum = 0;

        for (Product product : checkedProducts) {
            totalSum += product.getPrice();
        }

        textViewCartCount.setText("Выделено: " + count);
        textViewCartSum.setText("Итог: "  + totalSum + " BYN");
    }
}
