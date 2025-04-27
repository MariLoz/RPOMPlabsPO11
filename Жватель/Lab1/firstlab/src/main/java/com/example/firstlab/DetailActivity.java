package com.example.firstlab;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(getIntent().getExtras());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.detail_container, detailFragment)
                .commit();

        // Инициализация кнопки backButton
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Toast.makeText(this, "Нажал Жватель!", Toast.LENGTH_SHORT).show();
            // Можно добавить finish() если хотите закрыть активность при нажатии
            // finish();
        });
    }
}