package com.example.sixthlab;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary gLib;
    private GestureOverlayView gestures;
    private TextView tvOut;
    private StringBuilder inputNumber = new StringBuilder();
    private int targetNumber;
    private boolean isGameActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация TextView
        tvOut = findViewById(R.id.tvOut);

        // Генерация случайного числа от 0 до 100
        startNewGame();

        // Инициализация библиотеки жестов из файла gestures
        gLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gLib.load()) {
            finish();
        }

        // Инициализация GestureOverlayView и добавление слушателя
        gestures = findViewById(R.id.gestureOverlayView1);
        gestures.addOnGesturePerformedListener(this);

        // Инициализация новых кнопок
        Button taskButton = findViewById(R.id.taskButton);
        Button pressButton = findViewById(R.id.pressButton);

        // Обработчики кнопок
        taskButton.setOnClickListener(v -> showTaskDescription());
        pressButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Жватель!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gLib.recognize(gesture);

        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            if (prediction.score > 1.0) {
                if (isGameActive) {
                    handleGameGestures(prediction.name);
                } else {
                    handleNavigationGestures(prediction.name);
                }
            } else {
                tvOut.setText("Жест распознан недостаточно точно");
            }
        }
    }

    private void handleGameGestures(String gestureName) {
        switch (gestureName) {
            case "zero":
                inputNumber.append("0");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "one":
                inputNumber.append("1");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "two":
                inputNumber.append("2");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "three":
                inputNumber.append("3");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "four":
                inputNumber.append("4");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "five":
                inputNumber.append("5");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "six":
                inputNumber.append("6");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "seven":
                inputNumber.append("7");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "eight":
                inputNumber.append("8");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "nine":
                inputNumber.append("9");
                tvOut.setText("Введено: " + inputNumber.toString());
                break;
            case "stop":
                if (inputNumber.length() > 0) {
                    int userNumber = Integer.parseInt(inputNumber.toString());
                    checkGuess(userNumber);
                    inputNumber.setLength(0);
                } else {
                    tvOut.setText("Введите число перед завершением!");
                }
                break;
            case "info":
                showInfo();
                break;
            case "new":
                startNewGame();
                break;
            case "exit":
                finish();
                break;
            case "gestures":
                showGestureHelp();
                break;
            default:
                tvOut.setText("Жест неизвестен в игровом режиме");
                break;
        }
    }

    private void handleNavigationGestures(String gestureName) {
        switch (gestureName) {
            case "back":
                isGameActive = true;
                tvOut.setText("Введите число с помощью жестов");
                break;
            case "exit":
                finish();
                break;
            case "gestures":
                showGestureHelp();
                break;
            case "info":
                showInfo();
                break;
            default:
                tvOut.setText("Жест неизвестен в режиме информации");
                break;
        }
    }

    private void checkGuess(int userNumber) {
        if (userNumber == targetNumber) {
            tvOut.setText("Поздравляем, вы угадали число " + targetNumber + "!\nЖест 'new' для новой игры.");
        } else if (userNumber < targetNumber) {
            tvOut.setText("Ваше число " + userNumber + " меньше загаданного. Продолжайте!");
        } else {
            tvOut.setText("Ваше число " + userNumber + " больше загаданного. Продолжайте!");
        }
    }

    private void startNewGame() {
        Random random = new Random();
        targetNumber = random.nextInt(101);
        inputNumber.setLength(0);
        isGameActive = true;
        tvOut.setText("Новая игра началась! Введите число с помощью жестов.");
    }

    private void showInfo() {
        isGameActive = false;
        tvOut.setText("Это приложение созданное для лабораторной работы №16 'Угадайка', в которой реализовано загадывание случайного числа от 0 до 100, " +
                "пользователь может вводить цифры от 0 до 9, которые будут образовывать число. Для сравнения введённого числа с загаданным введите 'S'." +
                " Также в программе присутствуют другие жесты, информацию о которых можно получить введя '>'.");
    }

    private void showTaskDescription() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        final android.widget.PopupWindow popupWindow = new android.widget.PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        TextView instructionText = popupView.findViewById(R.id.instruction_text);
        instructionText.setText("В качестве практического задания предлагаем реализовать жестами ввод чисел в приложении 'Угадайка', " +
                "разработанном в лабораторной работе второй темы. Создать жесты '0' '1' '2' '3' '4' '5' '6' '7' '8' '9' " +
                "для ввода цифр и жест 'S' для остановки ввода числа. В приложение добавить распознавание этих жестов, " +
                "преобразование их в число и сравнение полученного числа с загаданным.\n\n" +
                "Бонусы (то, что способствует оценке выше 4):\n" +
                "• Разработка собственного СВЯЗНОГО набора жестов\n" +
                "• Представление в приложении дополнительной справочной информации (текст о назначении приложения)\n" +
                "• Вывод различных информационных сообщений после соответствующих жестов ПОЛЬЗОВАТЕЛЯ. " +
                "Подготовка сценария управления приложением с помощью жестов пользователя\n" +
                "• Возможность навигации по приложению с помощью набора жестов пользователя");

        Button okButton = popupView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(findViewById(R.id.gestureOverlayView1), Gravity.CENTER, 0, 0);
    }

    private void showGestureHelp() {
        isGameActive = false;
        tvOut.setText("Подсказка по жестам:\n" +
                "0-9: ввод цифр (доступен только во время игры)\n" +
                "'S': проверка числа (доступен только во время игры)\n" +
                "'N': новая игра (доступен только во время игры)\n" +
                "'?': информация\n" +
                "'o': выход\n" +
                "'<': возврат из меню\n" +
                "'>': эта подсказка");
    }
}