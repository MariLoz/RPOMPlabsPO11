package com.example.lab2.minishop;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab2.R;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ListView cartListView;
    private TextView totalSum;
    private Button cartBackButton;
    private CartAdapter cartAdapter;
    private ArrayList<Product> cartProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartListView = findViewById(R.id.cartListView);
        totalSum = findViewById(R.id.totalSum);
        cartBackButton = findViewById(R.id.cartBackButton);

        // Получаем товары из корзины
        cartProducts = Cart.getInstance().getProducts();

        // Устанавливаем адаптер для корзины
        cartAdapter = new CartAdapter(this, cartProducts);
        cartListView.setAdapter(cartAdapter);

        // Подсчет общей суммы товаров в корзине
        calculateTotal();

        // Обработка кнопки "Назад"
        cartBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем корзину и возвращаемся на главный экран
            }
        });
    }

    // Метод для подсчета общей суммы товаров в корзине
    private void calculateTotal() {
        int sum = 0;
        for (Product product : cartProducts) {
            sum += product.getPrice(); // Суммируем цены всех товаров в корзине
        }

        // Выводим общую сумму
        if (cartProducts.isEmpty()) {
            totalSum.setText("Корзина пуста!");
        } else {
            totalSum.setText("Сумма: " + sum + " руб.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // После возврата из корзины, пересчитываем сумму
        cartProducts = Cart.getInstance().getProducts();
        cartAdapter.notifyDataSetChanged(); // Обновляем адаптер
        calculateTotal(); // Пересчитываем сумму
    }
}
