package com.example.listdisplay;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList = new ArrayList<>();
    private Button btnLoadData;

    public ListFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        btnLoadData = view.findViewById(R.id.btnLoadData);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemAdapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(itemAdapter);

        btnLoadData.setOnClickListener(v -> {
            Log.d("BUTTON_CLICK", "Кнопка нажата, вызываем fetchHeroes()");
            fetchHeroes();
        });

        return view;
    }

    private void fetchHeroes() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Item>> call = apiService.getHeroes();

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    itemList.clear();
                    itemList.addAll(response.body());
                    itemAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Ошибка соединения: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
