package com.example.android_l1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private Button loadButton;
    private Button nextPageButton;
    private Button prevPageButton;
    private Button saveButton;
    private Button shareButton;
    private static final String JSON_URL = "https://gist.githubusercontent.com/DimaYanuschik/77165196104ed6b040e11cfc4e598e22/raw/41a212efa1d7ff1f82cf56c29a0ce0d368f81718/data.json";
    private ItemViewModel viewModel;
    private int currentPage = 0;
    private int itemsPerPage = 10;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        loadButton = view.findViewById(R.id.load_button);
        nextPageButton = view.findViewById(R.id.next_page_button);
        prevPageButton = view.findViewById(R.id.prev_page_button);
        saveButton = view.findViewById(R.id.save_button);
        shareButton = view.findViewById(R.id.share_button);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Button settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean showName = sharedPreferences.getBoolean("show_name", true);
        boolean showDescription = sharedPreferences.getBoolean("show_description", true);
        boolean showImage = sharedPreferences.getBoolean("show_image", true);

        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                updateRecyclerView(items, showName, showDescription, showImage);
            }
        });

        loadButton.setOnClickListener(v -> loadData());

        nextPageButton.setOnClickListener(v -> {
            currentPage++;
            updateRecyclerView(viewModel.getItems().getValue(), showName, showDescription, showImage);
        });

        prevPageButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateRecyclerView(viewModel.getItems().getValue(), showName, showDescription, showImage);
            }
        });

        saveButton.setOnClickListener(v -> saveToCSV());

        shareButton.setOnClickListener(v -> shareData());

        return view;
    }

    private void updateRecyclerView(List<Item> items, boolean showName, boolean showDescription, boolean showImage) {
        if (items != null) {
            int start = currentPage * itemsPerPage;
            int end = Math.min(start + itemsPerPage, items.size());
            List<Item> subList = items.subList(start, end);

            recyclerView.setAdapter(new ItemAdapter(subList, item -> {
                openDetailFragment(item);
            }, showName, showDescription, showImage));
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        String url = sharedPreferences.getString("last_url", JSON_URL);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://gist.githubusercontent.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getItems(url).enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> items = response.body();
                    int rows = sharedPreferences.getInt("rows_count", 10);
                    if (items.size() > rows) {
                        items = items.subList(0, rows);
                    }

                    boolean showName = sharedPreferences.getBoolean("show_name", true);
                    boolean showDescription = sharedPreferences.getBoolean("show_description", true);
                    boolean showImage = sharedPreferences.getBoolean("show_image", true);

                    viewModel.setItems(items);
                    updateRecyclerView(items, showName, showDescription, showImage);
                } else {
                    Toast.makeText(getContext(), "Ошибка: сервер вернул пустой ответ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка загрузки: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDetailFragment(Item item) {
        Bundle bundle = new Bundle();
        bundle.putString("title", item.getName());
        bundle.putString("description", item.getDescription());
        bundle.putString("image", item.getImageUrl());

        Fragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void saveToCSV() {
        List<Item> items = viewModel.getItems().getValue();
        if (items != null) {
            StringBuilder csvData = new StringBuilder();
            csvData.append("Name,Description,Image URL\n");
            for (Item item : items) {
                csvData.append(item.getName()).append(",")
                        .append(item.getDescription()).append(",")
                        .append(item.getImageUrl()).append("\n");
            }

            try {
                // Сохраняем файл в папку Downloads
                String fileName = "data.csv";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(csvData.toString().getBytes());
                fos.close();
                Toast.makeText(getContext(), "Данные сохранены в Downloads", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Ошибка: нет разрешения на запись файла", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Нет данных для сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareData() {
        List<Item> items = viewModel.getItems().getValue();
        if (items != null) {
            StringBuilder shareData = new StringBuilder();
            for (Item item : items) {
                shareData.append("Name: ").append(item.getName()).append("\n")
                        .append("Description: ").append(item.getDescription()).append("\n")
                        .append("Image URL: ").append(item.getImageUrl()).append("\n\n");
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareData.toString());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean showName = sharedPreferences.getBoolean("show_name", true);
        boolean showDescription = sharedPreferences.getBoolean("show_description", true);
        boolean showImage = sharedPreferences.getBoolean("show_image", true);

        if (viewModel.getItems().getValue() != null) {
            updateRecyclerView(viewModel.getItems().getValue(), showName, showDescription, showImage);
        }
    }
}
