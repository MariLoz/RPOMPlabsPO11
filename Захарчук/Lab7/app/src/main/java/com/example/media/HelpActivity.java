package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpText = findViewById(R.id.helpText);
        helpText.setText("Справка по приложению:\n\n" +
                "1. Главное меню: выберите активность (Музыка, Галерея, Справка, История).\n" +
                "2. Музыка: введите путь к медиафайлу (локальный или URL), управляйте воспроизведением (Start, Pause, Resume, Stop), используйте масштабирование.\n" +
                "3. Галерея: просматривайте фото из папки TrainingMedia, перелистывайте кнопками или свайпами, масштабируйте.\n" +
                "4. История: все действия сохраняются в базе данных.");

        findViewById(R.id.bBack).setOnClickListener(v -> finish());

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
    }
}