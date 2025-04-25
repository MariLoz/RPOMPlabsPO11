package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

public class TrainActivity extends AppCompatActivity implements OnGesturePerformedListener {

    private GestureLibrary gestureLib;
    private EditText numberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        numberInput = findViewById(R.id.numberInput);
        GestureOverlayView gestureOverlay = findViewById(R.id.gestureOverlay);
        gestureOverlay.addOnGesturePerformedListener(this);

        gestureLib = GestureLibraries.fromFile(getExternalFilesDir(null) + "/gestures");
        gestureLib.load();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        String number = numberInput.getText().toString();

        if (number.isEmpty() || !number.matches("[0-9]")) {
            Toast.makeText(this, "Введите цифру от 0 до 9", Toast.LENGTH_SHORT).show();
            return;
        }

        gestureLib.addGesture(number, gesture);
        gestureLib.save();

        Toast.makeText(this, "Жест для цифры " + number + " сохранён", Toast.LENGTH_SHORT).show();
        numberInput.setText("");
    }
}