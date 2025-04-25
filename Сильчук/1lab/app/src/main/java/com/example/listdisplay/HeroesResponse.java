package com.example.listdisplay;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HeroesResponse {
    @SerializedName("heroes")
    private List<Item> heroes;

    public List<Item> getHeroes() {
        return heroes != null ? heroes : List.of();
    }
}
