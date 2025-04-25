package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText jsonUrlInput;
    private Button saveButton;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        jsonUrlInput = findViewById(R.id.edittext_url);
        saveButton = findViewById(R.id.button_save_url);

        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String savedUrl = preferences.getString("json_url", "");
        jsonUrlInput.setText(savedUrl);

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String newUrl = jsonUrlInput.getText().toString().trim();

        if (newUrl.isEmpty()) {
            Toast.makeText(this, "Введите URL", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("json_url", newUrl);
        editor.apply();

        Toast.makeText(this, "Ссылка сохранена! Загружаем данные...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
