package com.example.lab1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {
    private LiteratureAdapter adapter;
    private List<Literature> literatureList;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button loadButton = findViewById(R.id.load_button);

        // Инициализация списка и адаптера
        literatureList = new ArrayList<>();
        adapter = new LiteratureAdapter(this, R.layout.item_literature, literatureList); // Передаем контекст, макет и список
        recyclerView.setAdapter(adapter);

        loadButton.setOnClickListener(v -> {
            // Загрузка данных
            loadData();
        });
    }

    public static boolean isValidUrl(String urlString) {
        try {
            new URL(urlString); // Пытаемся создать объект URL
            return true;
        } catch (MalformedURLException e) {
            return false; // Если строка не является корректным URL
        }
    }

    private void loadData() {
        editText = findViewById(R.id.editText);
        String url = editText.getText().toString();

        if (url.isEmpty()) {
            Toast.makeText(this, "пустой URL", Toast.LENGTH_LONG).show();

        } else if (!isValidUrl(url)) {
            Toast.makeText(this, "некорректный URL", Toast.LENGTH_LONG).show();
        } else {
            // Создаем запрос на получение JSON-массива
            @SuppressLint("NotifyDataSetChanged") JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, url, null,
                    this::onResponse,
                    Throwable::printStackTrace
            );

            // Добавляем запрос в очередь
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonArrayRequest);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onResponse(JSONArray response) {
        try {
// Очищаем список перед добавлением новых данных
            literatureList.clear();

// Парсим JSON и добавляем элементы в список
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                String name = jsonObject.getString("name");
                String info = jsonObject.getString("info");
                String bookUrl = jsonObject.getString("book");
                String author = jsonObject.getString("author");
                literatureList.add(new Literature(name, info, bookUrl, author));
            }

// Уведомляем адаптер об изменении данных
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}