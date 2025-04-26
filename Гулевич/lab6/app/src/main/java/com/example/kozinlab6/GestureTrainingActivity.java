package com.example.kozinlab6;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;

public class GestureTrainingActivity extends Activity {
    private GestureLibrary gestureLibrary;
    private GestureOverlayView gestureOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_training);

        gestureOverlayView = findViewById(R.id.gestureOverlay);
        Button saveGesture = findViewById(R.id.saveGesture);

        File gestureFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gestureFile);

        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show();
            return;
        }

        saveGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gestureOverlayView.getGesture() != null) {
                    Gesture gesture = gestureOverlayView.getGesture();
                    gestureLibrary.addGesture("new_gesture", gesture);
                    gestureLibrary.save();
                    Toast.makeText(GestureTrainingActivity.this, "Жест сохранен!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
