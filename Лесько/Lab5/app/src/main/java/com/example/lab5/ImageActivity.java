package com.example.lab5;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;


public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView imageView = findViewById(R.id.imageView);
        Button btnBackImage = findViewById(R.id.btnBackImage);

        Uri imageUri = getIntent().getData();
        imageView.setImageURI(imageUri);

        btnBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем активность и возвращаемся на предыдущий экран
            }
        });
    }
}
