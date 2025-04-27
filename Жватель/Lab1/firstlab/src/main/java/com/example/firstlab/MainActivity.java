package com.example.firstlab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListFragment listFragment;
    private List<Item> loadedItems = new ArrayList<>();
    private Button shareButton;
    private Button saveButton;
    private Button infoButton; // Новая кнопка для отображения информации о задаче

    // Информация о задаче и авторе
    private static final String TASK_DESCRIPTION = "Лабораторная работа №8\n" +
            " Реализовать интерфейс приложения для отображения списка элементов. " +
            " В качестве данных для списка использовать файл в формате JSON, загруженный с удаленного сервера. " +
            " Загрузка выполняется в ходе работы по команде пользователя, например, «Загрузить данные».\n" +
            " Приложение в минимальном исполнении должно:\n" +
            "• отображать список элементов внутри фрагмента\n" +
            "• список занимает более одного экрана (прокрутка)\n" +
            "• список можно пролистать\n" +
            "• отдельный элемент списка с пользовательским стилем/дизайном\n" +
            "• выполнять запрос на получение данных с удаленного сервера\n" +
            "• выполнять преобразование JSON-структуры в коллекцию объектов\n" +
            "• выделение отдельного элемента списка с отображением детальной информации на отдельном экране\n" +
            "• отображать детальную информацию об элементе внутри отдельного фрагмента\n" +
            " Бонусы (то, что способствует оценке выше 4):\n" +
            "• Преобразование и сохранение информации запроса (например, в текстовый файл или другой формат CSV, локально или в сеть…)\n" +
            "• Передача результатов запроса (электронная почта, мессенджер и т. п.)\n" +
            "• Разработка и использование собственного адаптера\n" +
            "• Включение изображений в список\n" +
            "• Обработка исключений с выводом сообщений.";
    private static final String AUTHOR_INFO = "Выполнил: Жватель Станислав Сергеевич\n" +
            "Группа: ПО-11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listFragment = new ListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, listFragment)
                .commit();

        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(v -> loadData());

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> shareResults());

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveResults());

        infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v -> showTaskInfo());
    }

    private void loadData() {
        String url = "https://api.myjson.online/v1/records/e9dad88f-bf54-4921-a75c-8d723d157939";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    List<Item> items = new ArrayList<>();
                    try {
                        JSONArray dataArray = response.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int id = obj.getInt("id");
                            String title = obj.getString("title");
                            String shortDescription = obj.getString("shortDescription");
                            String description = obj.getString("description");
                            String imageUrl = obj.getString("imageUrl");
                            items.add(new Item(id, title, shortDescription, description, imageUrl));
                        }
                        loadedItems.clear();
                        loadedItems.addAll(items);
                        listFragment.updateItems(items);
                        Toast.makeText(this, "Загружено " + items.size() + " элементов", Toast.LENGTH_SHORT).show();
                        shareButton.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Ошибка разбора JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        List<Item> testItems = new ArrayList<>();
                        for (int i = 1; i <= 10; i++) {
                            testItems.add(new Item(i, "Элемент " + i, "Краткое " + i,
                                    "Описание элемента " + i, "https://via.placeholder.com/150/000000/FFFFFF?text=Item+" + i));
                        }
                        loadedItems.clear();
                        loadedItems.addAll(testItems);
                        listFragment.updateItems(testItems);
                        shareButton.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(this, "Ошибка загрузки: " + (error.getMessage() != null ? error.getMessage() : "Неизвестная ошибка"), Toast.LENGTH_LONG).show();
                    List<Item> testItems = new ArrayList<>();
                    for (int i = 1; i <= 10; i++) {
                        testItems.add(new Item(i, "Элемент " + i, "Краткое " + i,
                                "Описание элемента " + i, "https://via.placeholder.com/150/000000/FFFFFF?text=Item+" + i));
                    }
                    loadedItems.clear();
                    loadedItems.addAll(testItems);
                    listFragment.updateItems(testItems);
                    shareButton.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                }
        );

        queue.add(jsonObjectRequest);
    }

    private void shareResults() {
        if (loadedItems.isEmpty()) {
            Toast.makeText(this, "Нет данных для отправки", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder shareText = new StringBuilder("Результаты запроса:\n\n");
        for (Item item : loadedItems) {
            shareText.append("ID: ").append(item.getId())
                    .append("\nЗаголовок: ").append(item.getTitle())
                    .append("\nОписание: ").append(item.getDescription())
                    .append("\nИзображение: ").append(item.getImageUrl())
                    .append("\n\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Результаты списка элементов");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "Поделиться результатами"));
    }

    private void saveResults() {
        if (loadedItems.isEmpty()) {
            Toast.makeText(this, "Нет данных для сохранения", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder saveText = new StringBuilder("Результаты запроса:\n\n");
        for (Item item : loadedItems) {
            saveText.append("ID: ").append(item.getId())
                    .append("\nЗаголовок: ").append(item.getTitle())
                    .append("\nОписание: ").append(item.getDescription())
                    .append("\nИзображение: ").append(item.getImageUrl())
                    .append("\n\n");
        }

        try {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, "RequestResults.txt");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(saveText.toString().getBytes());
            fos.close();
            Toast.makeText(this, "Данные сохранены в " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showTaskInfo() {
        String fullInfo = TASK_DESCRIPTION + "\n\n" + AUTHOR_INFO;
        new AlertDialog.Builder(this)
                .setTitle("Информация о задании")
                .setMessage(fullInfo)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }
}