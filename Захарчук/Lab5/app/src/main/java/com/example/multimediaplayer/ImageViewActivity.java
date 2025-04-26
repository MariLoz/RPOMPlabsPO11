package com.example.multimediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView = findViewById(R.id.imageView);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            imageView.setImageURI(intent.getData());
        }

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent taskIntent = new Intent(this, TaskInfoActivity.class);
            startActivity(taskIntent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
    }

    public void onBackToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}