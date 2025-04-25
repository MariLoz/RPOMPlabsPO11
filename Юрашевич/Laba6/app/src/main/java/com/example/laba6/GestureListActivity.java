package com.example.laba6;

import android.app.Activity;
import android.app.AlertDialog;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.Set;

public class GestureListActivity extends Activity {

    private GestureLibrary gestureLib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout container = findViewById(R.id.gestures_container);

        try {
            gestureLib = GestureLibraries.fromFile(new File(getFilesDir(), "app_gestures"));
            if (!gestureLib.load()) {
                Toast.makeText(this, "Библиотека жестов пуста", Toast.LENGTH_SHORT).show();
            }
            displayGestures(container, gestureLib);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки жестов", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayGestures(LinearLayout container, GestureLibrary gestureLib) {
        container.removeAllViews();

        Set<String> gestureEntries = gestureLib.getGestureEntries();
        if (gestureEntries == null || gestureEntries.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет сохраненных жестов");
            emptyText.setTextSize(18);
            container.addView(emptyText);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String name : gestureLib.getGestureEntries()) {
            for (Gesture gesture : gestureLib.getGestures(name)) {
                View gestureItem = inflater.inflate(R.layout.item_gesture, container, false);

                GestureOverlayView gestureView = gestureItem.findViewById(R.id.gesture_preview);
                gestureView.setGesture(gesture);

                TextView nameText = gestureItem.findViewById(R.id.gesture_name);
                nameText.setText(name);

                ImageButton deleteBtn = gestureItem.findViewById(R.id.delete_btn);
                deleteBtn.setOnClickListener(v -> showDeleteDialog(name, gestureLib, container));

                container.addView(gestureItem);
            }
        }
    }

    private void showDeleteDialog(String name, GestureLibrary gestureLib, LinearLayout container) {
        new AlertDialog.Builder(this)
                .setTitle("Удаление жеста")
                .setMessage("Удалить жест \"" + name + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    try {
                        gestureLib.removeEntry(name);
                        if (gestureLib.save()) {
                            displayGestures(container, gestureLib);
                            Toast.makeText(this, "Жест удален", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                        } else {
                            Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }
}