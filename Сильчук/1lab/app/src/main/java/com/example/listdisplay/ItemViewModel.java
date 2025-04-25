package com.example.listdisplay;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<List<Item>> items = new MutableLiveData<>();

    public LiveData<List<Item>> getItems() {
        return items;
    }

    public void fetchItems() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<List<Item>> call = apiService.getHeroes();

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(@NonNull Call<List<Item>> call, @NonNull Response<List<Item>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    items.setValue(response.body());
                    Log.d("API_SUCCESS", "Загружено: " + response.body().size() + " элементов");
                } else {
                    items.setValue(null);
                    Log.e("API_ERROR", "Ошибка загрузки данных: код " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Item>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Ошибка соединения: " + t.getMessage());
                items.setValue(null);
            }
        });
    }
}