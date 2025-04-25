package com.example.laba6;

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

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class GestureCreatorActivity extends Activity {
    private GestureLibrary gestureLib;
    private EditText gestureNameInput;
    private Gesture currentGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_creator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.creator), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gestureNameInput = findViewById(R.id.gesture_name);
        GestureOverlayView gestureCanvas = findViewById(R.id.gesture_canvas);
        Button saveBtn = findViewById(R.id.save_btn);

        try {
            gestureLib = GestureLibraries.fromFile(new File(getFilesDir(), "app_gestures"));
            if (!gestureLib.load()) {
                Toast.makeText(this, "Создана новая библиотека жестов", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки библиотеки жестов", Toast.LENGTH_SHORT).show();
            finish();
        }

        gestureCanvas.addOnGesturePerformedListener((overlay, gesture) -> {
            currentGesture = gesture;
            Toast.makeText(this, "Жест записан. Введите название и сохраните.", Toast.LENGTH_SHORT).show();
        });

        saveBtn.setOnClickListener(v -> saveGesture());
    }

    private void saveGesture() {
        String name = gestureNameInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Введите название жеста", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentGesture == null) {
            Toast.makeText(this, "Сначала нарисуйте жест", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            gestureLib.removeEntry(name);
            gestureLib.addGesture(name, currentGesture);

            if (gestureLib.save()) {
                Toast.makeText(this, "Жест '" + name + "' сохранен", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при сохранении жеста", Toast.LENGTH_SHORT).show();
        }
    }
}