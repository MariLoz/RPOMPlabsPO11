package com.example.touchcontrol;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.Set;

public class AddGestureActivity extends AppCompatActivity {

    private GestureOverlayView gestureOverlay;
    private EditText gestureNameInput;
    private GestureLibrary gestureLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gesture);

        // Инициализация GestureOverlayView
        gestureOverlay = findViewById(R.id.gestureOverlay);
        gestureNameInput = findViewById(R.id.gestureNameInput);

        // Настройка GestureOverlayView
        gestureOverlay.setGestureStrokeType(GestureOverlayView.GESTURE_STROKE_TYPE_MULTIPLE); // Разрешаем многократные штрихи
        gestureOverlay.setGestureColor(0xFFFFFF00); // Желтый цвет для рисования

        // Инициализация файла для хранения жестов
        File gesturesFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gesturesFile);

        // Создаем файл, если он не существует
        if (!gesturesFile.exists()) {
            try {
                gesturesFile.createNewFile();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка при создании файла жестов", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        // Загрузка жестов
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show();
        }

        // Сохранение жеста
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            String gestureName = gestureNameInput.getText().toString();
            if (gestureName.isEmpty()) {
                Toast.makeText(this, "Введите название жеста", Toast.LENGTH_SHORT).show();
                return;
            }

            // Проверка на уникальность имени жеста
            Set<String> gestureNames = gestureLibrary.getGestureEntries();
            if (gestureNames.contains(gestureName)) {
                Toast.makeText(this, "Жест с таким именем уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            // Получаем нарисованный жест
            Gesture gesture = gestureOverlay.getGesture();
            if (gesture != null) {
                gestureLibrary.addGesture(gestureName, gesture);
                if (gestureLibrary.save()) {
                    // Перезагружаем жесты
                    gestureLibrary.load();
                    Toast.makeText(this, "Жест сохранен", Toast.LENGTH_SHORT).show();
                    finish(); // Закрываем экран после сохранения
                } else {
                    Toast.makeText(this, "Ошибка при сохранении жеста", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Нарисуйте жест", Toast.LENGTH_SHORT).show();
            }
        });
    }
}