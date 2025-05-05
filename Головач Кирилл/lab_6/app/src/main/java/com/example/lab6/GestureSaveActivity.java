package com.example.lab6;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;

public class GestureSaveActivity extends Activity {
    private GestureLibrary gestureLibrary;
    private EditText gestureName;
    private Gesture lastGesture = null; // Сохраняем последний нарисованный жест

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_save);

        gestureName = findViewById(R.id.gesture_name);
        GestureOverlayView gestureOverlay = findViewById(R.id.gestureOverlay);
        Button saveButton = findViewById(R.id.save_gesture);

        // Файл, в который будут сохраняться жесты
        File gestureFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gestureFile);

        // Загружаем библиотеку жестов (если есть)
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Создана новая библиотека жестов", Toast.LENGTH_SHORT).show();
        }

        // Сохраняем последний нарисованный жест
        gestureOverlay.addOnGesturePerformedListener((overlay, gesture) -> {
            lastGesture = gesture;
            Toast.makeText(this, "Жест нарисован! Теперь введите имя и сохраните.", Toast.LENGTH_SHORT).show();
        });

        // Кнопка "Сохранить жест"
        saveButton.setOnClickListener(view -> {
            String name = gestureName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите имя жеста", Toast.LENGTH_SHORT).show();
                return;
            }

            if (lastGesture == null) {
                Toast.makeText(this, "Сначала нарисуйте жест!", Toast.LENGTH_SHORT).show();
                return;
            }

            gestureLibrary.removeEntry(name); // Удаляет все жесты с этим именем
            gestureLibrary.addGesture(name, lastGesture);
            gestureLibrary.save(); // Сохраняем библиотеку
            lastGesture = null; // Сбрасываем последний жест
            Toast.makeText(this, "Жест '" + name + "' сохранён!", Toast.LENGTH_SHORT).show();
        });
    }

}