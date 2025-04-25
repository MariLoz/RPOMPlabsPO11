package by.rpomp.lab_6;

import android.annotation.SuppressLint;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    private GestureLibrary mGestureLib;
    private TextView textView;
    private Button clearResultButton;
    private Map<String, String> gesturesMap = new HashMap<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        mGestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mGestureLib.load()) {
            finish();
            return;
        }
        setContentView(gestureOverlayView);

        gesturesMap.put("one", "1");
        gesturesMap.put("two", "2");
        gesturesMap.put("three", "3");
        gesturesMap.put("four", "4");
        gesturesMap.put("five", "5");
        gesturesMap.put("six", "6");
        gesturesMap.put("seven", "7");
        gesturesMap.put("eight", "8");
        gesturesMap.put("nine", "9");
        gesturesMap.put("zero", "0");
        gesturesMap.put("plus", "+");
        gesturesMap.put("minus", "-");
        gesturesMap.put("umnozit", "*");
        gesturesMap.put("delit", "/");
        gesturesMap.put("ravno", "=");
        textView = findViewById(R.id.result);
        clearResultButton = findViewById(R.id.clearResultButton);
        textView.setText("");
        clearResultButton.setOnClickListener(view -> {
            textView.setText("");
        });

    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mGestureLib.recognize(gesture);
        if (!predictions.isEmpty() && predictions.get(0).score > 1.0) {
            Toast.makeText(this, "Распознан жест: " + predictions.get(0).name, Toast.LENGTH_SHORT).show();

            String gesture_ = gesturesMap.getOrDefault(predictions.get(0).name, "default");
            if (Objects.equals(gesture_, "=")) {
                textView.setText(String.valueOf(evaluateExpression(String.valueOf(textView.getText()))));
            } else {
                textView.setText(textView.getText() + gesture_);
            }
        }
    }

    public static double evaluateExpression(String expression){
        expression = expression.replaceAll("\\s+", "");
        double result = 0;
        char operator = ' ';
        StringBuilder number = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else if ("+-*/".indexOf(c) >= 0) {
                if (operator != ' ') {
                    result = calculate(result, operator, Double.parseDouble(number.toString()));
                } else {
                    result = Double.parseDouble(number.toString());
                }
                operator = c;
                number.setLength(0);
            }
        }
        if (operator != ' ') {
            result = calculate(result, operator, Double.parseDouble(number.toString()));
        }
        return result;
    }

    public static double calculate(double left, char operator, double right){
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                if (right == 0) return -1;
                return left / right;
            default:
                return -1;
        }
    }
}