package com.example.lab7;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        ImageView imageView = findViewById(R.id.imageView);
        Button btnBack = findViewById(R.id.btnBack);

        // Получаем URI изображения из Intent
        Uri imageUri = getIntent().getData();

        // Отображаем изображение с помощью Glide
        if (imageUri != null) {
            Glide.with(this).load(imageUri).into(imageView);
        }

        // Обработка нажатия на кнопку "Назад"
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем активность и возвращаемся на предыдущий экран
            }
        });
    }
}