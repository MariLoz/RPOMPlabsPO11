package com.example.lab1;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
public interface ApiService {
    @GET("blob:https://opendata.blender.org/d31c2123-e8ca-4fe1-b24e-e1b32a6135c2")
    Call<JsonObject> getData();
}
