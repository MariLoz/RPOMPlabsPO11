package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity
        implements GestureOverlayView.OnGesturePerformedListener {

    public enum Difficulty {
        EASY(5, 15000, true, "Легкий"),
        MEDIUM(3, 10000, false, "Средний"),
        HARD(1, 5000, false, "Сложный");

        final int maxAttempts;
        final long timeLimit;
        final boolean showRangeHint;
        final String title;

        Difficulty(int attempts, long time, boolean hint, String title) {
            this.maxAttempts = attempts;
            this.timeLimit = time;
            this.showRangeHint = hint;
            this.title = title;
        }
    }

    private GestureLibrary gestureLib;
    private TextView messageText;
    private TextView attemptsText;
    private TextView timerText;
    private TextView difficultyText;
    private Button restartButton;
    private ConfettiView confettiView;

    private Difficulty currentDifficulty;
    private CountDownTimer timer;
    private int secretNumber;
    private int attempts;
    private int lowerBound = 0;
    private int upperBound = 9;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        try {
            initViews();
            loadDifficulty();
            loadGestures();
            startNewGame();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("GameActivity", "Error: ", e);
            finish();
        }
    }

    private void initViews() {
        messageText = findViewById(R.id.messageText);
        attemptsText = findViewById(R.id.attemptsText);
        timerText = findViewById(R.id.timerText);
        difficultyText = findViewById(R.id.difficultyText);

        GestureOverlayView gestureOverlay = findViewById(R.id.gestureOverlay);
        gestureOverlay.addOnGesturePerformedListener(this);

        try {
            confettiView = findViewById(R.id.confettiView);
        } catch (Exception e) {
            Log.w("GameActivity", "ConfettiView not found");
        }
    }

    private void loadDifficulty() {
        String diffName = getIntent().getStringExtra("DIFFICULTY");
        if (diffName == null) {
            throw new IllegalStateException("Difficulty not specified");
        }
        currentDifficulty = Difficulty.valueOf(diffName);
        difficultyText.setText(currentDifficulty.title);
    }

    private void loadGestures() {
        File gesturesFile = new File(getExternalFilesDir(null), "gestures");
        if (!gesturesFile.exists()) {
            throw new IllegalStateException("Gesture library not found");
        }

        gestureLib = GestureLibraries.fromFile(gesturesFile);
        if (!gestureLib.load()) {
            throw new IllegalStateException("Could not load gestures");
        }
    }

    private void startNewGame() {
        secretNumber = random.nextInt(10);
        attempts = 0;
        lowerBound = 0;
        upperBound = 9;

        if (timer != null) {
            timer.cancel();
        }

        if (currentDifficulty.timeLimit > 0) {
            timer = new CountDownTimer(currentDifficulty.timeLimit, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer((int) (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    handleGameLoss();
                }
            }.start();
        } else {
            timerText.setText("Без лимита");
        }

        confettiView.setVisibility(View.INVISIBLE);
        confettiView.stopConfetti();

        messageText.setText("Нарисуйте цифру от 0 до 9");
        updateUI();
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<android.gesture.Prediction> predictions = gestureLib.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String predictedNumber = predictions.get(0).name;

            try {
                int guessedNumber = Integer.parseInt(predictedNumber);
                checkGuess(guessedNumber);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Пожалуйста, рисуйте только цифры", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Жест не распознан. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
        }
    }



    private void checkGuess(int guessedNumber) {
        attempts++;

        if (currentDifficulty.showRangeHint) {
            if (guessedNumber < secretNumber) {
                lowerBound = Math.max(lowerBound, guessedNumber + 1);
            } else if (guessedNumber > secretNumber) {
                upperBound = Math.min(upperBound, guessedNumber - 1);
            }
        }

        if (guessedNumber == secretNumber) {
            handleGameWin();
        } else if (attempts >= currentDifficulty.maxAttempts) {
            handleGameLoss();
        } else {
            String hint = guessedNumber < secretNumber ? "больше" : "меньше";
            messageText.setText("Неверно! Загаданное число " + hint + " чем " + guessedNumber);
            updateUI();
        }
    }

    private void handleGameWin() {
        if (timer != null) {
            timer.cancel();
        }

        messageText.setText(String.format("Победа! Это было число %d", secretNumber));
        confettiView.setVisibility(View.VISIBLE);
        confettiView.startConfetti();

        updateUI();

        new Handler().postDelayed(this::startNewGame, 3000);
    }

    private void handleGameLoss() {
        if (timer != null) {
            timer.cancel();
        }

        messageText.setText(String.format("Игра окончена. Загаданное число: %d", secretNumber));
        updateUI();

        new Handler().postDelayed(this::startNewGame, 3000);
    }

    private void updateUI() {
        String rangeHint = currentDifficulty.showRangeHint ?
                String.format(" (%d-%d)", lowerBound, upperBound) : "";

        attemptsText.setText(String.format("Попыток: %d/%d%s",
                attempts,
                currentDifficulty.maxAttempts,
                rangeHint));
    }

    private void updateTimer(int secondsLeft) {
        timerText.setText(String.format("Осталось: %d сек", secondsLeft));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}