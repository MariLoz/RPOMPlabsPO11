package com.example.rpomp_l6;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.gesture.GestureOverlayView.OnGesturePerformedListener;

public class GestureTrainingActivity extends AppCompatActivity implements OnGesturePerformedListener {
    private EditText gestureName;
    private GestureOverlayView gestureOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_training);

        GestureHelper.initialize(this);

        gestureName = findViewById(R.id.gestureName);
        gestureOverlay = findViewById(R.id.gestureOverlay);
        gestureOverlay.addOnGesturePerformedListener(this);

        findViewById(R.id.saveButton).setOnClickListener(v -> saveGesture());
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        // Можно добавить предварительный просмотр жеста
    }

    private void saveGesture() {
        String name = gestureName.getText().toString().trim().toLowerCase();

        if (name.isEmpty()) {
            Toast.makeText(this, "Введите имя жеста", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка на конфликт с существующими жестами
        if (GestureHelper.isDefaultGesture(name)) {
            Toast.makeText(this, "Этот жест уже существует в системе", Toast.LENGTH_SHORT).show();
            return;
        }

        Gesture gesture = gestureOverlay.getGesture();
        if (gesture != null) {
            GestureHelper.addGesture(name, gesture);
            Toast.makeText(this, "Жест '" + name + "' сохранен", Toast.LENGTH_SHORT).show();
            gestureName.setText("");
            gestureOverlay.clear(false);
        } else {
            Toast.makeText(this, "Нарисуйте жест сначала", Toast.LENGTH_SHORT).show();
        }
    }
}