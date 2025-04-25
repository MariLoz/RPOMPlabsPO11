package com.example.lab6;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;

public class GestureSaveActivity extends Activity {
    private GestureLibrary gestureLibrary;
    private Gesture lastGesture = null; // Сохраняем последний нарисованный жест

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_save);

        String[] gesturesList = {"one","two","three", "four", "five","six","seven","eight","nine","zero","plus","minus","multiply","divide"};

        Spinner spinner = findViewById(R.id.gesture_name);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, gesturesList);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

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
            String name = spinner.getSelectedItem().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите имя жеста", Toast.LENGTH_SHORT).show();
                return;
            }

            if (lastGesture == null) {
                Toast.makeText(this, "Сначала нарисуйте жест!", Toast.LENGTH_SHORT).show();
                return;
            }

            gestureLibrary.addGesture(name, lastGesture);
            gestureLibrary.save(); // Сохраняем библиотеку
            lastGesture = null; // Сбрасываем последний жест
            Toast.makeText(this, "Жест '" + name + "' сохранён!", Toast.LENGTH_SHORT).show();
        });
    }

}