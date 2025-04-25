package com.example.lab6;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GesturePanelActivity extends AppCompatActivity {
    private ArrayList<String> gesturesArrayList;
    private TextView counterGestures;
    private TextView infoOfRecognizedGesture;
    private GestureOverlayView gestureOverlay;
    private GestureLibrary gestureLibrary;
    private ArrayAdapter<String> arrayAdapter;
    private final StringBuilder userInput = new StringBuilder();
    private int secretNumber;
    Button checkButton, addGestureButton;
    private ActivityResultLauncher<Intent> saveGestureActivityLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gesture_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainR), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gestureOverlay = findViewById(R.id.gestureOverlay);
        counterGestures = findViewById(R.id.count_of_gesture);
        infoOfRecognizedGesture = findViewById(R.id.info_of_recognized_gesture);
        ListView listOfGestures = findViewById(R.id.list_of_gestures);

        checkButton = findViewById(R.id.check_button);
        addGestureButton = findViewById(R.id.add_gesture_button);

        File gestureFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gestureFile);
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Ошибка загрузки библиотеки жестов", Toast.LENGTH_SHORT).show();
        }

        String[] gesturesArray = gestureLibrary.getGestureEntries().toArray(new String[0]);
        gesturesArrayList = new ArrayList<>();
        gesturesArrayList.addAll(Arrays.asList(gesturesArray));

        secretNumber = new Random().nextInt(100);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.textView, gesturesArrayList);
        listOfGestures.setAdapter(arrayAdapter);
        listOfGestures.setOnItemClickListener((parent, view, position, id) -> readGesture(position));
        listOfGestures.setOnItemLongClickListener((parent, view, position, id) -> {
            dialogConfirmDelete(position);
            return true;
        });

        refresh_UI();

        addGestureButton.setOnClickListener(v -> {
            Intent intent = new Intent(GesturePanelActivity.this, GestureSaveActivity.class);
            saveGestureActivityLauncher.launch(intent);
        });

        gestureOverlay.setGestureStrokeAngleThreshold(90.0f);
        gestureOverlay.addOnGesturePerformedListener((overlay, gesture) -> {
            List<Prediction> predictions = gestureLibrary.recognize(gesture);
            if (predictions.isEmpty()){
                Toast.makeText(this, "Нет жестов", Toast.LENGTH_SHORT).show();
            }
            if (!predictions.isEmpty() && predictions.get(0).score > 2.0) {
                Prediction recognizedGesture = predictions.get(0);
                handleGesture(recognizedGesture);
            }
            else {
                Toast.makeText(this, "Жест не распознан", Toast.LENGTH_SHORT).show();
            }
        });

        checkButton.setOnClickListener(v -> checkNumber());

        saveGestureActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String newGestureName = data.getStringExtra("newGestureName");
                            gesturesArrayList.add(newGestureName);
                            refresh_UI();
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.lab_info_settings) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh_UI() {
        counterGestures.setText("Количество жестов:");
        counterGestures.append(" " + gesturesArrayList.size());
        arrayAdapter.notifyDataSetChanged();
        gestureOverlay.clear(false);
    }

    public void readGesture(int positionGesture) {
        String gestureEntry = gesturesArrayList.get(positionGesture);
        Gesture gesture = gestureLibrary.getGestures(gestureEntry).get(0);
        gestureOverlay.setGesture(gesture);
    }

    public void deleteGesture(int positionGesture) {
        gestureLibrary.removeEntry(gesturesArrayList.get(positionGesture));
        gestureLibrary.save();
        gesturesArrayList.remove(positionGesture);
    }

    private void dialogConfirmDelete(int positionGesture) {
        new MaterialAlertDialogBuilder(this)
                .setMessage("Подтвердите удаление. Вы уверены?")
                .setNeutralButton("Закрыть", (dialog, which) -> dialog.cancel())
                .setPositiveButton("Принять", (dialog, which) ->
                {
                    Toast.makeText(getApplicationContext(), "Жест удалён: " + gesturesArrayList.get(positionGesture), Toast.LENGTH_SHORT).show();
                    deleteGesture(positionGesture);
                    refresh_UI();
                })
                .show();
    }

    private void checkNumber() {
        if (userInput.length() == 0) {
            Toast.makeText(this, "Введите число", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int enteredNumber = Integer.parseInt(userInput.toString());
            if (enteredNumber == secretNumber) {
                Toast.makeText(this, "Правильно! Загаданное число: " + secretNumber, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Неправильно! Загаданное число: " + secretNumber, Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка: Введено не число!", Toast.LENGTH_SHORT).show();
        } finally {
            userInput.setLength(0);
            infoOfRecognizedGesture.setText("Введите число жестами");
            secretNumber = new Random().nextInt(100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Создана новая библиотека жестов", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleGesture(@NonNull Prediction recognizedGesture) {
        switch (recognizedGesture.name) {
            case "exit":
                Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case "create":
                Toast.makeText(this, "Создание жеста", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, GestureSaveActivity.class));
                break;
            case "menu":
                Toast.makeText(this, "Открытие меню", Toast.LENGTH_SHORT).show();
                openOptionsMenu();
                break;
            case "clear":
                userInput.setLength(0);
                infoOfRecognizedGesture.setText("Введите число жестами");
                Toast.makeText(this, "Ввод сброшен", Toast.LENGTH_SHORT).show();
                break;
            case "about app":
                Intent intent = new Intent(this, InfoActivity.class);
                startActivity(intent);
                break;
            default: {
                if (recognizedGesture.name.equals("check")) {
                    checkNumber();
                } else {
                    userInput.append(recognizedGesture.name);
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    infoOfRecognizedGesture.setText("Обнаруженный жест: " + recognizedGesture.name + "\nСчёт: " + formatter.format(recognizedGesture.score) + "\nОбщее число: " + userInput);
                }
            }
        }
    }
}