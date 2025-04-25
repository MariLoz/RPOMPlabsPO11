package com.example.lab7;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelectFile = findViewById(R.id.btnSelectFile);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);

        // Кнопка для выбора файла
        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                filePickerLauncher.launch(intent);
            }
        });

        // Кнопка для создания снимка
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent); // Переход на CameraActivity
            }
        });

        // Инициализация ActivityResultLauncher для выбора файла
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            handleSelectedFile(fileUri); // Обрабатываем выбранный файл
                        }
                    }
                });
    }

    private void handleSelectedFile(Uri fileUri) {
        // Определение типа файла и открытие соответствующей активности
        String mimeType = getContentResolver().getType(fileUri);
        if (mimeType != null) {
            if (mimeType.startsWith("image")) {
                Intent intent = new Intent(this, ImageActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            } else if (mimeType.startsWith("video")) {
                Intent intent = new Intent(this, VideoActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            } else if (mimeType.startsWith("audio")) {
                Intent intent = new Intent(this, AudioActivity.class);
                intent.setData(fileUri);
                startActivity(intent);
            }
        }
    }
}