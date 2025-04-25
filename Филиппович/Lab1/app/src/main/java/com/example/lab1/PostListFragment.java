package com.example.lab1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PostListFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> allPosts = new ArrayList<>();
    private List<Post> currentPosts = new ArrayList<>();

    private Button prevPageButton, nextPageButton;
    private TextView pageIndicator, authorInfo;
    private Spinner rowCountSpinner;
    private Switch viewToggleSwitch;

    private int currentPage = 1;
    private int rowsPerPage = 10;
    private boolean isGridView = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        prevPageButton = view.findViewById(R.id.prevPageButton);
        nextPageButton = view.findViewById(R.id.nextPageButton);
        pageIndicator = view.findViewById(R.id.pageIndicator);
        rowCountSpinner = view.findViewById(R.id.rowCountSpinner);
        viewToggleSwitch = view.findViewById(R.id.viewToggleSwitch);
        authorInfo = view.findViewById(R.id.authorInfo);

        authorInfo.setText("Разработала: Филиппович Милана Сергеевна\nГруппа: ПО-11");

        setupRecyclerView();
        setupListeners();
        loadPosts();

        return view;
    }

    private void setupRecyclerView() {
        updateLayoutManager();
        adapter = new PostAdapter(currentPosts, this::openPostDetail);
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadPosts();
            }
        });

        nextPageButton.setOnClickListener(v -> {
            currentPage++;
            loadPosts();
        });

        rowCountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rowsPerPage = Integer.parseInt(parent.getItemAtPosition(position).toString());
                currentPage = 1;
                loadPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        viewToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isGridView = isChecked;
            updateLayoutManager();
            adapter.notifyDataSetChanged();
        });
    }

    private void updateLayoutManager() {
        if (isGridView) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private void loadPosts() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.artic.edu/api/v1/artworks?page=" + currentPage + "&limit=" + rowsPerPage;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonString = response.body().string();

                    // Логирование JSON-ответа для отладки
                    Log.d("PostListFragment", "Response JSON: " + jsonString);

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    JsonArray dataArray = jsonObject.getAsJsonArray("data");

                    List<Post> newPosts = new ArrayList<>();
                    for (int i = 0; i < dataArray.size(); i++) {
                        JsonObject item = dataArray.get(i).getAsJsonObject();

                        // Проверяем, чтобы title не был null
                        String title = (item.has("title") && !item.get("title").isJsonNull())
                                ? item.get("title").getAsString()
                                : "Без названия";

                        // Проверяем, чтобы image_id не был null
                        String imageId = (item.has("image_id") && !item.get("image_id").isJsonNull())
                                ? item.get("image_id").getAsString()
                                : "";

                        // Формируем URL изображения
                        String imageUrl = imageId.isEmpty() ? ""
                                : "https://www.artic.edu/iiif/2/" + imageId + "/full/843,/0/default.jpg";

                        Post post = new Post(0, title, imageUrl);
                        newPosts.add(post);
                    }

                    requireActivity().runOnUiThread(() -> {
                        allPosts = newPosts;
                        currentPosts.clear();
                        currentPosts.addAll(allPosts);
                        adapter.notifyDataSetChanged();
                        pageIndicator.setText("Страница " + currentPage);
                    });
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void openPostDetail(Post post) {
        PostDetailFragment fragment = PostDetailFragment.newInstance(post.getTitle(), post.getImageUrl());
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
