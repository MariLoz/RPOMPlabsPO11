package com.example.kozinlab6;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureLibrary;
import android.gesture.GestureLibraries;
import android.gesture.Prediction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    private GestureLibrary gestureLibrary;
    private EditText resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = findViewById(R.id.resultView);
        GestureOverlayView gestureOverlayView = findViewById(R.id.gestureOverlay);
        gestureOverlayView.addOnGesturePerformedListener(this);

        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLibrary.load()) {
            finish();
        }
        Button calculateButton = findViewById(R.id.btnCalculate);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = resultView.getText().toString().trim();
                if (!input.isEmpty()) {
                    try {
                        double result = eval(input);
                        resultView.setText(String.valueOf(result));
                    } catch (Exception e) {
                        resultView.setText("Ошибка");
                    }
                }
            }
        });

        Button helpButton = findViewById(R.id.btnHelp);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog();
            }
        });
    }

    private void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Справка");
        builder.setMessage("Калькулятор поддерживает следующие операции:\n\n"
                + "- Сложение (+)\n"
                + "- Вычитание (-)\n"
                + "- Умножение (*)\n"
                + "- Деление (/)\n\n"
                + "Можно вводить числа и операции как вручную, так и с помощью жестов.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        List<Prediction> predictions = gestureLibrary.recognize(gesture);
        if (!predictions.isEmpty() && predictions.get(0).score > 2.0) {
            String command = predictions.get(0).name;

            String currentText = resultView.getText().toString();

            if (command.equals("equal")) {
                calculateResult();
            } else if (command.equals("clear")) {
                resultView.setText("");
            } else {
                resultView.setText(currentText + command);
                resultView.setSelection(resultView.getText().length()); // Курсор в конец
            }
        }
    }

    private void calculateResult() {
        try {
            String expr = resultView.getText().toString();
            double result = eval(expr);
            resultView.setText(String.valueOf(result));
        } catch (Exception e) {
            resultView.setText("Ошибка");
        }
    }

    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < expr.length()) ? expr.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();
                double x;
                int startPos = this.pos;
                if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }
        }.parse();
    }
}
