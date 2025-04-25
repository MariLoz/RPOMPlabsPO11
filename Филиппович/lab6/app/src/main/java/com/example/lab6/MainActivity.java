package com.example.lab6;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureLibraries;
import android.gesture.GestureOverlayView;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GestureOverlayView gestureOverlayView;
    private TextView resultTextView;
    private GestureLibrary gestureLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        gestureOverlayView = findViewById(R.id.gestureOverlayView);
        resultTextView = findViewById(R.id.resultTextView);

        // Создаем библиотеку жестов
        gestureLibrary = GestureLibraries.fromPrivateFile(this, "gestures");
        if (!gestureLibrary.load()) {
            // Если файл жестов не загружен, создаем жесты программно
            createGestures();
        }

        // Обработка жестов
        gestureOverlayView.addOnGesturePerformedListener((gestureOverlayView, gesture) -> {
            var predictions = gestureLibrary.recognize(gesture);
            for (var prediction : predictions) {
                Log.d("GestureApp", "Name: " + prediction.name + ", Score: " + prediction.score);
            }
            if (predictions.size() > 0 && predictions.get(0).score > 1.0) { // Увеличьте порог
                switch (predictions.get(0).name) {
                    case "1":
                        resultTextView.setText("Вы нарисовали 1");
                        openInfoActivity();
                        break;
                    case "2":
                        resultTextView.setText("Вы нарисовали 2");
                        changeBackgroundColor();
                        break;
                    case "3":
                        resultTextView.setText("Вы нарисовали 3");
                        increaseVolume(); // Теперь жест 3 увеличивает громкость
                        break;
                    case "4":
                        resultTextView.setText("Вы нарисовали 4");
                        decreaseVolume(); // Теперь жест 4 уменьшает громкость
                        break;
                    case "5":
                        resultTextView.setText("Вы нарисовали 5");
                        playSound();
                        break;
                    default:
                        resultTextView.setText("Жест не распознан");
                        break;
                }
            } else {
                resultTextView.setText("Жест не распознан");
            }
        });
    }

    private void createGestures() {
        // Жест для цифры "1" (вертикальная линия с крючком)
        Gesture gesture1 = new Gesture();
        gesture1.addStroke(createStroke(new float[]{50, 50, 50, 200, 50, 200, 100, 200})); // Вертикальная линия с крючком
        gestureLibrary.addGesture("1", gesture1);

        // Жест для цифры "2" (зигзаг с петлей)
        Gesture gesture2 = new Gesture();
        gesture2.addStroke(createStroke(new float[]{50, 50, 200, 50, 50, 200, 200, 200, 200, 200, 150, 150})); // Зигзаг с петлей
        gestureLibrary.addGesture("2", gesture2);

        // Жест для цифры "3" (диагональная линия)
        Gesture gesture3 = new Gesture();
        gesture3.addStroke(createStroke(new float[]{50, 50, 200, 200})); // Диагональная линия
        gestureLibrary.addGesture("3", gesture3);

        // Жест для цифры "4" (крест)
        Gesture gesture4 = new Gesture();
        gesture4.addStroke(createStroke(new float[]{50, 100, 200, 100, 125, 50, 125, 200})); // Крест
        gestureLibrary.addGesture("4", gesture4);

        // Жест для цифры "5" (зигзаг с дополнительными элементами)
        Gesture gesture5 = new Gesture();
        gesture5.addStroke(createStroke(new float[]{
                50, 50, 200, 50, 50, 125, 200, 125, 50, 200, 200, 200, // Зигзаг
                200, 200, 150, 150, 200, 100 // Дополнительные элементы
        }));
        gestureLibrary.addGesture("5", gesture5);

        // Сохраняем жесты
        gestureLibrary.save();
    }

    private GestureStroke createStroke(float[] points) {
        ArrayList<GesturePoint> gesturePoints = new ArrayList<>();
        for (int i = 0; i < points.length; i += 2) {
            gesturePoints.add(new GesturePoint(points[i], points[i + 1], System.currentTimeMillis()));
        }
        return new GestureStroke(gesturePoints);
    }

    private void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    private void changeBackgroundColor() {
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent)); // Измените цвет фона
    }

    private void playSound() {
        showToast("Звук воспроизведен");
    }

    private void increaseVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // Увеличиваем громкость медиа-потока на один шаг
            audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC, // Управляем громкостью медиа-потока
                    AudioManager.ADJUST_RAISE, // Увеличиваем громкость
                    AudioManager.FLAG_SHOW_UI // Показываем системный UI для громкости
            );
            showToast("Громкость увеличена");
        }
    }

    private void decreaseVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // Уменьшаем громкость медиа-потока на один шаг
            audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC, // Управляем громкостью медиа-потока
                    AudioManager.ADJUST_LOWER, // Уменьшаем громкость
                    AudioManager.FLAG_SHOW_UI // Показываем системный UI для громкости
            );
            showToast("Громкость уменьшена");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}