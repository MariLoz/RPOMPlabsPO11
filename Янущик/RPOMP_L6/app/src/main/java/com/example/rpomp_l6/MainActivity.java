package com.example.rpomp_l6;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {
    private TextView hintText;
    private TextView inputDisplay;
    private GestureOverlayView gestureOverlay;
    private Button restartButton;

    private int secretNumber;
    private StringBuilder currentInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Инициализация GestureHelper
        GestureHelper.initialize(this);

        // Инициализация UI
        hintText = findViewById(R.id.hintText);
        inputDisplay = findViewById(R.id.inputDisplay);
        gestureOverlay = findViewById(R.id.gestureOverlay);
        restartButton = findViewById(R.id.restartButton);

        // Настройка обработчика жестов
        gestureOverlay.addOnGesturePerformedListener(this);

        // Начало новой игры
        startNewGame();

        // Кнопка перезапуска игры
        restartButton.setOnClickListener(v -> startNewGame());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_train) {
            startActivity(new Intent(this, GestureTrainingActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        String action = GestureHelper.recognizeGesture(gesture);

        if (action != null) {
            processGestureAction(action);
        }
    }

    private void processGestureAction(String action) {
        Toast.makeText(this, "Жест: " + action, Toast.LENGTH_SHORT).show();

        switch (action) {
            case "s":
                checkGuess();
                break;
            case "question":
                showHelpDialog();
                break;
            case "right":
                startNewGame();
                break;
            case "left":
                // Навигация назад
                onBackPressed();
                break;
            case "up":
                startActivity(new Intent(this, GestureTrainingActivity.class));
                break;
            default:
                if (action.matches("[0-9]")) {
                    if (currentInput.length() < 3) {
                        currentInput.append(action);
                        updateInputDisplay();
                        inputDisplay.setTextColor(Color.GREEN);
                        inputDisplay.postDelayed(() -> inputDisplay.setTextColor(Color.BLACK), 200);
                    } else {
                        Toast.makeText(this, "Максимум 3 цифры", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void checkGuess() {
        if (currentInput.length() == 0) return;

        try {
            int guess = Integer.parseInt(currentInput.toString());

            if (guess == secretNumber) {
                showResultDialog(true, guess);
            } else {
                hintText.setText(guess < secretNumber ?
                        "Загаданное число "+ secretNumber + " больше " + guess : "Загаданное число " + secretNumber + " меньше " + guess);
                currentInput.setLength(0);
                updateInputDisplay();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка ввода числа", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResultDialog(boolean isWin, int number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (isWin) {
            builder.setTitle("Поздравляем!")
                    .setMessage(getString(R.string.win_message, number))
                    .setPositiveButton(R.string.try_again, (dialog, which) -> startNewGame())
                    .setNegativeButton(R.string.exit, (dialog, which) -> finish());
        }

        builder.show();
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.gesture_help_title)
                .setMessage(R.string.gesture_help_text)
                .setPositiveButton("OK", null)
                .show();
    }

    private void startNewGame() {
        secretNumber = new Random().nextInt(100) + 1;
        currentInput.setLength(0);
        updateInputDisplay();
        hintText.setText("Попробуйте угадать число от 1 до 100");
    }

    private void updateInputDisplay() {
        inputDisplay.setText(currentInput.length() > 0 ? currentInput.toString() : "—");
    }
}