package com.example.android_l1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<List<Item>> items = new MutableLiveData<>();

    public void setItems(List<Item> itemList) {
        items.setValue(itemList); // Убираем проверку на null
    }

    public LiveData<List<Item>> getItems() {
        return items;
    }
}
