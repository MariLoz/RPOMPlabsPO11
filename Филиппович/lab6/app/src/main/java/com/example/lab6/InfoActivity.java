package com.example.lab6;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Находим TextView и задаем текст с информацией
        TextView infoText = findViewById(R.id.infoText);
        infoText.setText("Добро пожаловать в приложение!\n\n" +
                "Это приложение позволяет управлять жестами:\n" +
                "1 - Открыть информацию\n" +
                "2 - Изменить цвет фона\n" +
                "3 - Увеличить громкость\n" +
                "3 - Уменьшить громкость");
    }
}