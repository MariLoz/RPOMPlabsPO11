package com.example.lab6;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class GestureSaveActivity extends AppCompatActivity {
    private Gesture newGesture;
    private String gestureName;
    private GestureOverlayView gestureOverlay;
    private GestureLibrary gestureLibrary;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gesture_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainGesture), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button clearButton = findViewById(R.id.clean_overlay_button);
        clearButton.setOnClickListener(v -> gestureOverlay.clear(false));
        Button saveButton = findViewById(R.id.save_gesture_button);
        saveButton.setOnClickListener(this::saveCreateGesture);
        gestureOverlay = findViewById(R.id.gestureOverlay);

        File gestureFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gestureFile);
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Создана новая библиотека жестов", Toast.LENGTH_SHORT).show();
        }
    }
        public void saveCreateGesture(View view) {
            dialogAskNameGesture();
        }

    private void dialogAskNameGesture() {
        newGesture = gestureOverlay.getGesture();
        if (newGesture == null || newGesture.getStrokesCount() == 0) {
            Toast.makeText(this, "Сначала нарисуйте какой-нибудь жест!", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText editText = new EditText(this);
        new MaterialAlertDialogBuilder(this)
                .setTitle("Введите имя жеста")
                .setView(editText)
                .setNegativeButton("Закрыть", (dialog, which) -> {
                    dialog.cancel();
                    gestureOverlay.clear(false);
                })
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String ToastMessage;
                    gestureName = editText.getText().toString();

                    if (gestureName.isEmpty()) {
                        ToastMessage = "Имя не должно быть пустое!";
                        dialogAskNameGesture();
                    } else {
                        createGesture(gestureName, newGesture);
                        ToastMessage = "Жест '" + gestureName + "' добавлен";
                        gestureOverlay.clear(false);

                        Intent intent = new Intent();
                        intent.putExtra("newGestureName", gestureName);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), ToastMessage, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void createGesture(String gestureName, Gesture gesture) {
        gestureLibrary.addGesture(gestureName, gesture);
        gestureLibrary.save();
    }
}