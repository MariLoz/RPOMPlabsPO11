package com.example.lab2.headphones_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.R;
public class HeadphonesDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productName, productDescription, productPrice;
    private ImageButton backButton;
    private Headphones headphones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headphones_details);

        productImage = findViewById(R.id.productImage);
        productName = findViewById(R.id.productName);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        backButton = findViewById(R.id.backButton);

        // Получаем данные из интента
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("headphones")) {
            headphones = (Headphones) intent.getSerializableExtra("headphones");

            productName.setText(headphones.getName());
            productDescription.setText(headphones.getDescription());
            productPrice.setText("Цена: " + headphones.getPrice() + " BYN");

            productImage.setImageResource(headphones.getImageResId());

        }

        // Кнопка назад
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Возвращаемся на предыдущий экран
            }
        });

    }
}
