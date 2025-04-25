package com.example.listdisplay;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api/heroStats")
    Call<List<Item>> getHeroes();
}