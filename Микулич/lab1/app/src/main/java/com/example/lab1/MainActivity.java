package com.example.lab1;

import android.os.Bundle;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PhonesAdapter adapter;
    private List<Phones> guitarAmpList = new ArrayList<>();
    private Spinner spinnerViewType;
    private Button loadJsonButton, openJsonButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerItemsView);
        spinnerViewType = findViewById(R.id.spinner);
        loadJsonButton = findViewById(R.id.loadJsonButton);
        openJsonButton = findViewById(R.id.openJsonButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Линейный список", "Сетка", "Справка"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerViewType.setAdapter(spinnerAdapter);

        // Обработчик выбора в Spinner
        spinnerViewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Линейный список
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                } else if (position == 1) {
                    // Сетка (2 колонки)
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                } else if (position == 2) {
                    // Открываем окно со справкой
                    openInfoActivity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делать
            }
        });
        int layoutType = getIntent().getIntExtra("layoutType", -1);
        if (layoutType == 0) {
            spinnerViewType.setSelection(0);
        } else if (layoutType == 1) {
            spinnerViewType.setSelection(1);
        }
        ArrayList<Phones> receivedList = (ArrayList<Phones>) getIntent().getSerializableExtra("ampList");
        if (receivedList != null) {
            guitarAmpList = receivedList;
            updateRecyclerView();
        }
        loadJsonButton.setOnClickListener(v -> loadJsonFromUrl("https://github.com/Perhewz-Hellcat/android-lab/raw/main/ign1.json"));
        openJsonButton.setOnClickListener(v -> selectJsonFile());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // Метод для загрузки JSON по ссылке
    private void loadJsonFromUrl(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Ошибка загрузки JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
                Log.e("JSON_LOAD", "Ошибка: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                String jsonData = response.body().string();
                parseJson(jsonData);

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "JSON успешно загружен!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Метод для обработки JSON
    private void parseJson(String jsonData) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Phones>>() {}.getType();
        guitarAmpList = gson.fromJson(jsonData, listType);

        runOnUiThread(() -> {
            adapter = new PhonesAdapter(this, guitarAmpList, amp -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("ampList", (ArrayList<Phones>) guitarAmpList);
                intent.putExtra("index", guitarAmpList.indexOf(amp));
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        });
    }

    // Метод для выбора JSON-файла через проводник
    private void selectJsonFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        jsonFileLauncher.launch(intent);
    }

    // Обработчик результата выбора файла
    private final ActivityResultLauncher<Intent> jsonFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    readJsonFromFile(uri);
                }
            });

    // Чтение JSON-файла после выбора
    private void readJsonFromFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            parseJson(jsonBuilder.toString());

            runOnUiThread(() -> {
                Toast.makeText(this, "JSON-файл успешно загружен!", Toast.LENGTH_SHORT).show();
            });

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
            Log.e("JSON_FILE", "Ошибка: " + e.getMessage());
        }
    }
    private void updateRecyclerView() {
        adapter = new PhonesAdapter(this, guitarAmpList, amp -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("ampList", (ArrayList<Phones>) guitarAmpList);
            intent.putExtra("index", guitarAmpList.indexOf(amp));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }
    private void openInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("ampList", (ArrayList<Phones>) guitarAmpList);
        startActivity(intent);
    }
}