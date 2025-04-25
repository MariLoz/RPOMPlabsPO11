package com.example.laba1;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("VictorYrman/crypto-json/main/cryptocurrencies.json")
    Call<CryptoResponse> getCryptocurrencies();
}