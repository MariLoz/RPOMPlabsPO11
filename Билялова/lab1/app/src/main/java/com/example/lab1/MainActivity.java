package com.example.lab1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab1.adapter.ItemAdapter;
import com.example.lab1.model.Item;
import com.example.lab1.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button loadButton, toggleLayoutButton, prevButton, nextButton;
    private TextView headerText, pageNumberText;
    private ApiService apiService;
    private boolean isGrid = false;
    private List<Item> items = new ArrayList<>();
    private ItemAdapter adapter;
    private int page = 1; // Номер страницы
    private final int itemsPerPage = 10; // Количество элементов на странице

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerText = findViewById(R.id.header_text);
        headerText.setText("Билялова Александра, ПО-11");

        recyclerView = findViewById(R.id.recycler_view);
        loadButton = findViewById(R.id.load_button);
        toggleLayoutButton = findViewById(R.id.toggle_layout_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        pageNumberText = findViewById(R.id.page_number);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        loadButton.setOnClickListener(v -> loadItems());

        toggleLayoutButton.setOnClickListener(v -> {
            isGrid = !isGrid;
            recyclerView.setLayoutManager(isGrid ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this));
        });

        prevButton.setOnClickListener(v -> {
            if (page > 1) {
                page--;
                updateRecyclerView();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (page * itemsPerPage < items.size()) {
                page++;
                updateRecyclerView();
            }
        });
    }

    private void loadItems() {
        apiService.getItems().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items = response.body();
                    updateRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void updateRecyclerView() {
        int start = (page - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, items.size());

        adapter = new ItemAdapter(items.subList(start, end), this, item -> {
            Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("imageUrl", item.getImageUrl());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        pageNumberText.setText("Страница: " + page);
    }
}
