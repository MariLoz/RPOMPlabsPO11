package com.example.touchcontrol;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GestureOverlayView gestureOverlay;
    private TextView resultText;
    private GestureLibrary gestureLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureOverlay = findViewById(R.id.gestureOverlay);
        resultText = findViewById(R.id.resultText);

        // Инициализация файла для хранения жестов
        File gesturesFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gesturesFile);

        // Загрузка жестов
        if (!gestureLibrary.load()) {
            Toast.makeText(this, R.string.failed_to_load_gestures, Toast.LENGTH_SHORT).show();
        }

        // Обработка жестов
        gestureOverlay.addOnGesturePerformedListener((overlay, gesture) -> {
            ArrayList<Prediction> predictions = gestureLibrary.recognize(gesture);
            if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
                String gestureName = predictions.get(0).name;
                resultText.setText(getString(R.string.result, gestureName));
            } else {
                resultText.setText(R.string.gesture_not_recognized);
            }
        });

        // Переход на экран добавления жестов
        findViewById(R.id.addGestureButton).setOnClickListener(v -> {
            startActivityForResult(new Intent(MainActivity.this, AddGestureActivity.class), 1);
        });

        // Переход на экран управления жестами
        findViewById(R.id.manageGesturesButton).setOnClickListener(v -> {
            startActivityForResult(new Intent(MainActivity.this, ManageGesturesActivity.class), 2);
        });

        // Кнопка для отображения информации о программе
        findViewById(R.id.infoButton).setOnClickListener(v -> {
            showInfoDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем жесты каждый раз, когда возвращаемся на главный экран
        gestureLibrary.load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Перезагружаем жесты после возврата с экранов добавления или управления
        if (resultCode == RESULT_OK) {
            gestureLibrary = GestureLibraries.fromFile(new File(getFilesDir(), "gestures")); // Пересоздаем GestureLibrary
            gestureLibrary.load(); // Принудительно загружаем жесты
        }
    }

    // Отображение диалога с информацией о программе
    private void showInfoDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.about_program)
                .setMessage(R.string.program_info)
                .setPositiveButton(R.string.ok, null)
                .show();
    }
}