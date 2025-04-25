package com.example.lab6;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button trainButton = findViewById(R.id.trainButton);
        Button playButton = findViewById(R.id.playButton);

        trainButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TrainActivity.class));
            Toast.makeText(this, "Нажал Сымоник И.А.", Toast.LENGTH_SHORT).show();
        });

        playButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DifficultySelectorActivity.class));
            Toast.makeText(this, "Нажал Сымоник И.А.", Toast.LENGTH_SHORT).show();

        });
    }
}