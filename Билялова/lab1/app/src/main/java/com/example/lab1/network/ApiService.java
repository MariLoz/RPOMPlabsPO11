package com.example.lab1.network;

import com.example.lab1.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("breeds")
    Call<List<Item>> getItems();
}
