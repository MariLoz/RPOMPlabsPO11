package com.example.gestureapp;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import android.gesture.Prediction;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    private GestureLibrary gestureLib;
    private TextView textView;
    private GestureOverlayView gestureOverlayView;
    private String lastGesture = "";
    private ArrayList<String> notes = new ArrayList<>();
    private int currentNoteIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        gestureOverlayView = findViewById(R.id.gestureOverlayView);
        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        if (textView == null || gestureOverlayView == null) {
            finish();
            return;
        }

        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
            textView.setText("Ошибка загрузки жестов");
            finish();
        }

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            if (prediction.score > 1.0) {
                String gestureName = prediction.name;
                lastGesture = gestureName;
                switch (gestureName) {
                    case "circle":
                        textView.setText("GestureApp - управление заметками через жесты\n" +
                                "Доступные жесты:\n" +
                                "Круг: эта справка\n" +
                                "Линия вправо: следующая заметка\n" +
                                "Линия влево: предыдущая заметка\n" +
                                "S: сохранить заметку\n" +
                                "X: удалить заметку\n" +
                                "N: новая заметка\n" +
                                "Зигзаг: открыть меню\n" +
                                "Треугольник: информация о последнем жесте");
                        break;
                    case "right":
                        if (currentNoteIndex < notes.size() - 1) {
                            currentNoteIndex++;
                            textView.setText("Заметка " + (currentNoteIndex + 1) + ": " + notes.get(currentNoteIndex));
                        } else {
                            textView.setText("Нет следующей заметки");
                        }
                        break;
                    case "left":
                        if (currentNoteIndex > 0) {
                            currentNoteIndex--;
                            textView.setText("Заметка " + (currentNoteIndex + 1) + ": " + notes.get(currentNoteIndex));
                        } else {
                            textView.setText("Нет предыдущей заметки");
                        }
                        break;
                    case "save":
                        if (currentNoteIndex >= 0) {
                            textView.setText("Заметка " + (currentNoteIndex + 1) + " сохранена");
                        } else {
                            textView.setText("Сначала создайте заметку");
                        }
                        break;
                    case "delete":
                        if (currentNoteIndex >= 0 && !notes.isEmpty()) {
                            notes.remove(currentNoteIndex);
                            currentNoteIndex = Math.min(currentNoteIndex, notes.size() - 1);
                            textView.setText("Заметка удалена. Осталось: " + notes.size());
                        } else {
                            textView.setText("Нет заметок для удаления");
                        }
                        break;
                    case "new":
                        notes.add("Новая заметка " + (notes.size() + 1));
                        currentNoteIndex = notes.size() - 1;
                        textView.setText("Создана заметка " + (currentNoteIndex + 1));
                        break;
                    case "menu":
                        textView.setText("Меню открыто (в разработке)");
                        break;
                    case "info":
                        textView.setText("Последний жест: " + (lastGesture.isEmpty() ? "неизвестен" : lastGesture));
                        break;
                    default:
                        textView.setText("Жест распознан, но не поддерживается: " + gestureName);
                        break;
                }
            } else {
                textView.setText("Жест неизвестен (низкий score)");
            }
        } else {
            textView.setText("Жест не распознан");
        }
    }
}