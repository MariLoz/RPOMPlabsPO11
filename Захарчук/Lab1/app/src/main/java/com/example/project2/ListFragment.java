package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    private EditText serverUrl, linesCount;
    private Spinner requestSpinner;
    private String[] requestOptions = {"items.json", "items2.json", "items3.json"};
    private ActivityResultLauncher<Intent> saveFileLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saveFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                        try {
                            OutputStream outputStream = requireActivity().getContentResolver()
                                    .openOutputStream(result.getData().getData());
                            if (outputStream == null) {
                                throw new IOException("Не удалось открыть поток для записи");
                            }
                            writeCsvToOutputStream(outputStream);
                            outputStream.close();
                            Toast.makeText(getContext(), "Файл успешно сохранен", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Ошибка сохранения файла: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Неизвестная ошибка при сохранении: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Сохранение отменено пользователем", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        try {
            view = inflater.inflate(R.layout.fragment_list, container, false);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка загрузки интерфейса: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        try {
            serverUrl = view.findViewById(R.id.serverUrl);
            linesCount = view.findViewById(R.id.linesCount);
            requestSpinner = view.findViewById(R.id.requestSpinner);
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, requestOptions);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            requestSpinner.setAdapter(spinnerAdapter);

            adapter = new ItemAdapter(requireContext(), itemList, item -> {
                try {
                    DetailFragment detailFragment = new DetailFragment();
                    Bundle args = new Bundle();
                    args.putString("title", item.getTitle());
                    args.putString("description", item.getDescription());
                    args.putString("imageUrl", item.getImageUrl());
                    detailFragment.setArguments(args);
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, detailFragment)
                            .addToBackStack(null)
                            .commit();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Ошибка при открытии деталей: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, getMaxLines());
            recyclerView.setAdapter(adapter);

            Button loadButton = view.findViewById(R.id.loadButton);
            loadButton.setOnClickListener(v -> loadData());

            Button saveButton = view.findViewById(R.id.saveButton);
            saveButton.setOnClickListener(v -> saveToCsv());

            Button shareButton = view.findViewById(R.id.shareButton);
            shareButton.setOnClickListener(v -> shareData());

            Button authorButton = view.findViewById(R.id.authorButton);
            authorButton.setOnClickListener(v -> Toast.makeText(getContext(), "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

            // Добавляем обработку кнопки "О задаче"
            Button taskInfoButton = view.findViewById(R.id.taskInfoButton);
            taskInfoButton.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), TaskInfoActivity.class);
                startActivity(intent);
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка инициализации интерфейса: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return view;
        }

        return view;
    }

    private int getMaxLines() {
        try {
            String linesText = linesCount.getText().toString();
            if (linesText.isEmpty()) {
                throw new NumberFormatException("Поле количества строк пустое");
            }
            return Integer.parseInt(linesText);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Неверный формат числа строк: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return 2;
        }
    }

    private void loadData() {
        try {
            String baseUrl = serverUrl.getText().toString().trim();
            if (baseUrl.isEmpty()) {
                throw new IllegalArgumentException("URL сервера не указан");
            }
            String selectedRequest = requestSpinner.getSelectedItem().toString();
            String fullUrl = baseUrl + selectedRequest;

            RequestQueue queue = Volley.newRequestQueue(requireContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                    response -> {
                        try {
                            if (response == null || response.isEmpty()) {
                                throw new JSONException("Пустой ответ от сервера");
                            }
                            JSONArray jsonArray = new JSONArray(response);
                            itemList.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Item item = new Item();
                                item.setId(obj.getInt("id"));
                                item.setTitle(obj.getString("title"));
                                item.setDescription(obj.getString("description"));
                                item.setImageUrl(obj.optString("imageUrl", "")); // Загружаем URL изображения
                                itemList.add(item);
                            }
                            adapter = new ItemAdapter(requireContext(), itemList, adapter.listener, getMaxLines());
                            recyclerView.setAdapter(adapter);
                            Toast.makeText(getContext(), "Данные успешно загружены", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Ошибка обработки JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Неизвестная ошибка при загрузке: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    error -> Toast.makeText(getContext(), "Ошибка сети: " + error.getMessage(), Toast.LENGTH_LONG).show());

            queue.add(stringRequest);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при запросе данных: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveToCsv() {
        try {
            if (itemList.isEmpty()) {
                throw new IllegalStateException("Нет данных для сохранения");
            }
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, "items_" + System.currentTimeMillis() + ".csv");
            saveFileLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при запуске сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeCsvToOutputStream(OutputStream outputStream) throws IOException {
        try {
            StringBuilder csvContent = new StringBuilder("ID,Title,Description,ImageURL\n");
            for (Item item : itemList) {
                csvContent.append(item.toCsv()).append("\n");
            }
            outputStream.write(csvContent.toString().getBytes());
        } catch (Exception e) {
            throw new IOException("Ошибка записи CSV: " + e.getMessage());
        }
    }

    private void shareData() {
        try {
            if (itemList.isEmpty()) {
                throw new IllegalStateException("Нет данных для отправки");
            }
            StringBuilder content = new StringBuilder("ID,Title,Description,ImageURL\n");
            for (Item item : itemList) {
                content.append(item.toCsv()).append("\n");
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Данные из Project2");
            shareIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
            startActivity(Intent.createChooser(shareIntent, "Поделиться данными"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при отправке данных: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}