package com.example.laba6;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GestureLibrary gestureLib;
    private Gesture currentGesture;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        resultText = findViewById(R.id.result_text);
        GestureOverlayView gesturePad = findViewById(R.id.gesture_pad);
        Button checkBtn = findViewById(R.id.check_btn);
        Button addGestureBtn = findViewById(R.id.add_gesture_btn);
        Button listGesturesBtn = findViewById(R.id.list_gestures_btn);

        initGestureLibrary();

        addGestureBtn.setOnClickListener(v ->
                startActivityForResult(new Intent(this, GestureCreatorActivity.class), 1)
        );

        listGesturesBtn.setOnClickListener(v ->
                startActivityForResult(new Intent(this, GestureListActivity.class), 1)
        );

        gesturePad.addOnGesturePerformedListener((overlay, gesture) -> {
            currentGesture = gesture;
            resultText.setText("Жест нарисован. Нажмите 'Проверить'");
        });

        checkBtn.setOnClickListener(v -> checkGesture());
    }

    private void initGestureLibrary() {
        reloadGestureLibrary();
    }

    private void reloadGestureLibrary() {
        try {
            gestureLib = GestureLibraries.fromFile(new File(getFilesDir(), "app_gestures"));
            if (!gestureLib.load()) {
                Toast.makeText(this, "Библиотека жестов пуста", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки библиотеки жестов", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            reloadGestureLibrary();
        }
    }
    private void checkGesture() {
        if (currentGesture == null) {
            resultText.setText("Сначала нарисуйте жест");
            return;
        }

        try {
            List<Prediction> predictions = gestureLib.recognize(currentGesture);

            if (predictions != null && !predictions.isEmpty()) {
                Prediction bestPrediction = predictions.get(0);
                if (bestPrediction.score > 2.0) {
                    String gestureName = bestPrediction.name;
                    resultText.setText("Распознанный жест: " + gestureName);
                } else {
                    resultText.setText("Жест не распознан");
                }
            } else {
                resultText.setText("Такого жеста нет в библиотеке");
            }
        } catch (Exception e) {
            resultText.setText("Ошибка распознавания");
            Toast.makeText(this, "Ошибка при распознавании жеста", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gestureLib != null && !gestureLib.load()) {
            Toast.makeText(this, "Ошибка загрузки библиотеки жестов", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}