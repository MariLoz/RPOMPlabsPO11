package com.example.lab7;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private float scale = 1.0f; // Начальный масштаб

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        Button btnBackImage = findViewById(R.id.btnBackImage);
        Button btnZoomIn = findViewById(R.id.btnZoomIn);
        Button btnZoomOut = findViewById(R.id.btnZoomOut);

        Uri imageUri = getIntent().getData();
        imageView.setImageURI(imageUri);

        btnBackImage.setOnClickListener(v -> finish()); // Кнопка назад

        // Увеличение изображения
        btnZoomIn.setOnClickListener(v -> {
            scale += 0.2f;
            imageView.setScaleX(scale);
            imageView.setScaleY(scale);
        });

        // Уменьшение изображения
        btnZoomOut.setOnClickListener(v -> {
            if (scale > 0.5f) { // Чтобы не уменьшалось слишком сильно
                scale -= 0.2f;
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            }
        });
    }
}
