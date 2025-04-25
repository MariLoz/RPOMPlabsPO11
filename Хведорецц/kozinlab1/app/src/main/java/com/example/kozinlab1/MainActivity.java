-=package com.example.kozinlab1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import android.net.Uri;
import java.io.Serializable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity {


    private static final int PICK_JSON_FILE = 1;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> items;
    private PostAdapter postAdapter;
    private List<Post> posts;

    // Инициализация Retrofit
    private Retrofit retrofit;
    private ApiService apiService;

    // Инициализация ActivityResultLauncher для открытия файла
    private final ActivityResultLauncher<Intent> openFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        String jsonContent = readJsonFromFile(uri);

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Item>>() {}.getType();
                        items = gson.fromJson(jsonContent, listType);

                        if (items != null && !items.isEmpty()) {
                            itemAdapter = new ItemAdapter(items, item -> {
                                int position = items.indexOf(item);
                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                intent.putExtra("description", item.getDescription());
                                intent.putExtra("imageUrl", item.getImageUrl());
                                intent.putExtra("id", item.getId()); // Передаем id текущего элемента
                                intent.putExtra("detailedDescription", item.getDetailedDescription());
                                intent.putExtra("items", (Serializable) items); // Передаем список элементов
                                startActivity(intent);
                            });
                            recyclerView.setAdapter(itemAdapter);
                        } else {
                            Toast.makeText(this, "Файл не содержит данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLoadJson = findViewById(R.id.button);
        Button btnLinearLayout = findViewById(R.id.buttonLinear);
        Button btnGridLayout = findViewById(R.id.buttonGrid);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Инициализация Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Вызов метода для получения данных с сервера
        fetchItemsFromServer();

        btnLoadJson.setOnClickListener(v -> openFilePicker());

        btnLinearLayout.setOnClickListener(v -> {
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this)); // Устанавливаем LinearLayoutManager
            itemAdapter.notifyDataSetChanged(); // Обновляем адаптер
        });

        btnGridLayout.setOnClickListener(v -> {
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2)); // Устанавливаем GridLayoutManager с 2 столбцами
            itemAdapter.notifyDataSetChanged(); // Обновляем адаптер
        });
    }

    private void fetchItemsFromServer() {
        Call<List<Post>> call = apiService.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    posts = response.body();
                    if (posts != null && !posts.isEmpty()) {
                        postAdapter = new PostAdapter(posts);
                        recyclerView.setAdapter(postAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Список товаров пуст", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка при получении данных с сервера", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        openFileLauncher.launch(intent); // Используем ActivityResultLauncher
    }

    private String readJsonFromFile(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}