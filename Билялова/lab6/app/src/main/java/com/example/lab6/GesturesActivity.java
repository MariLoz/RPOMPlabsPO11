package com.example.lab6;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GesturesActivity extends AppCompatActivity implements GlassGestureDetector.OnGestureListener {
    private GlassGestureDetector glassGestureDetector;
    private TextView label;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestures);

        label = findViewById(R.id.textView);
        rootLayout = findViewById(R.id.root_layout);
        glassGestureDetector = new GlassGestureDetector(this, this);
    }

    @Override
    public boolean onGesture(GlassGestureDetector.Gesture gesture) {
        switch (gesture) {
            case TAP:
                label.setText("Нажатие");
                rootLayout.setBackgroundColor(Color.parseColor("#FF9800"));
                return true;
            case DOUBLE_TAP:
                label.setText("Двойное нажатие");
                rootLayout.setBackgroundColor(Color.parseColor("#673AB7"));
                return true;
            case SWIPE_FORWARD:
                label.setText("Свайп влево");
                rootLayout.setBackgroundColor(Color.parseColor("#03A9F4"));
                return true;
            case SWIPE_BACKWARD:
                label.setText("Свайп вправо");
                rootLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
                return true;
            case SWIPE_UP:
                label.setText("Свайп вверх");
                rootLayout.setBackgroundColor(Color.parseColor("#F44336"));
                return true;
            case SWIPE_DOWN:
                label.setText("Свайп вниз");
                rootLayout.setBackgroundColor(Color.parseColor("#FFC107"));
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return glassGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }
}
