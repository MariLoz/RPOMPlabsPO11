package com.example.lab6;

import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private GestureLibrary gestureLibrary;
    private TextView numberDisplay;
    private StringBuilder userInput = new StringBuilder();
    private int secretNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Отключаем стандартный заголовок
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Устанавливаем кастомный заголовок
        View customView = getLayoutInflater().inflate(R.layout.custom_actionbar, null);
        getSupportActionBar().setCustomView(customView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        numberDisplay = findViewById(R.id.number_display);
        GestureOverlayView gestureOverlay = findViewById(R.id.gestureOverlay);
        Button checkButton = findViewById(R.id.check_button);
        Button addGestureButton = findViewById(R.id.add_gesture_button); // Кнопка для добавления жестов

        // Загружаем библиотеку жестов
        File gestureFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gestureFile);

        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Ошибка загрузки библиотеки жестов", Toast.LENGTH_SHORT).show();
        }

        // Генерируем случайное число
        secretNumber = new Random().nextInt(100);

        // Открытие GestureSaveActivity
        addGestureButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GestureSaveActivity.class);
            startActivity(intent);
        });

        // Обработчик жестов
        gestureOverlay.addOnGesturePerformedListener((overlay, gesture) -> {
            List<Prediction> predictions = gestureLibrary.recognize(gesture);

            if (!predictions.isEmpty() && predictions.get(0).score > 2.0) {
                String recognizedGesture = predictions.get(0).name;
                handleGesture(recognizedGesture);

            } else {
                Toast.makeText(this, "Жест не распознан", Toast.LENGTH_SHORT).show();
            }
        });

        // Проверка введённого числа
        checkButton.setOnClickListener(v -> checkNumber());
    }

    private void checkNumber() {
        if (userInput.length() == 0) {
            Toast.makeText(this, "Введите букву", Toast.LENGTH_SHORT).show();
            return;
        }

        userInput.setLength(0);
        numberDisplay.setText("Введите букву");
        secretNumber = new Random().nextInt(100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Загружаем библиотеку жестов (если есть)
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Создана новая библиотека жестов", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGesture(String gestureName) {
        switch (gestureName) {
            case "exit":
                Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case "create":
                Toast.makeText(this, "Создание жеста", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, GestureSaveActivity.class));
                break;
            case "menu":
                Toast.makeText(this, "Открытие меню", Toast.LENGTH_SHORT).show();
                openOptionsMenu();
                break;

            case "clear":
                userInput.setLength(0);
                numberDisplay.setText(null);
                Toast.makeText(this, "Ввод сброшен", Toast.LENGTH_SHORT).show();
                break;
            case "ok":
                checkNumber();
                break;
            default: {
                if (gestureName.equals("S")) {
                    checkNumber();
                } else {
                    userInput.append(gestureName);
                    numberDisplay.setText(userInput.toString());
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.labinfo_settings) {
            Intent intent = new Intent(this, LabinfoActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}