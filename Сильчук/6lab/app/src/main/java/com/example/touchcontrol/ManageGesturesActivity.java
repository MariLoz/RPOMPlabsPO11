package com.example.touchcontrol;

import android.gesture.GestureLibrary;
import android.gesture.GestureLibraries;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class ManageGesturesActivity extends AppCompatActivity {

    private GestureLibrary gestureLibrary;
    private ListView gestureList;
    private Button deleteButton;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> gestureListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_gestures);

        gestureList = findViewById(R.id.gestureList);
        deleteButton = findViewById(R.id.deleteButton);

        // Инициализация файла для хранения жестов
        File gesturesFile = new File(getFilesDir(), "gestures");
        gestureLibrary = GestureLibraries.fromFile(gesturesFile);

        // Загрузка жестов
        if (!gestureLibrary.load()) {
            Toast.makeText(this, "Не удалось загрузить жесты", Toast.LENGTH_SHORT).show();
        }

        // Отображение списка жестов
        Set<String> gestureNames = gestureLibrary.getGestureEntries();
        gestureListItems = new ArrayList<>(gestureNames);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, gestureListItems);
        gestureList.setAdapter(adapter);
        gestureList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Разрешаем выбор нескольких элементов

        // Удаление выбранных жестов
        deleteButton.setOnClickListener(v -> {
            // Получаем выбранные элементы
            ArrayList<String> selectedGestures = new ArrayList<>();
            for (int i = 0; i < gestureList.getCount(); i++) {
                if (gestureList.isItemChecked(i)) {
                    selectedGestures.add(gestureListItems.get(i));
                }
            }

            if (selectedGestures.isEmpty()) {
                Toast.makeText(this, "Выберите жесты для удаления", Toast.LENGTH_SHORT).show();
                return;
            }

            // Удаляем выбранные жесты
            for (String gestureName : selectedGestures) {
                gestureLibrary.removeEntry(gestureName);
            }

            // Сохраняем изменения
            if (gestureLibrary.save()) {
                gestureListItems.removeAll(selectedGestures);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Жесты удалены", Toast.LENGTH_SHORT).show();

                // Возвращаем результат в MainActivity
                setResult(RESULT_OK);
            } else {
                Toast.makeText(this, "Ошибка при удалении жестов", Toast.LENGTH_SHORT).show();
            }
        });
    }
}