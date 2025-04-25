package com.example.lab2.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.R;
public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice;
    private Button addToCartButton, backButton;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        addToCartButton = findViewById(R.id.addToCartButton);
        backButton = findViewById(R.id.backButton);

        // Получаем данные из интента
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product")) {
            product = (Product) intent.getSerializableExtra("product");

            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText("Цена: " + product.getPrice() + " руб.");

            productImage.setImageResource(product.getImageResId());

        }

        // Кнопка назад
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Возвращаемся на предыдущий экран
            }
        });

        // Кнопка добавить в корзину
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.getInstance().addProduct(product);
                finish(); // Закрываем активность после добавления товара
            }
        });
    }
}
