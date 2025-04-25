package by.rpomp.lab_1.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiService {
    @GET("mars-photos/api/v1/rovers/curiosity/photos")
    Call<PhotoResponse> getPhotos(@QueryMap Map<String, String> options);

    @GET("planetary/apod")
    Call<AstronomyPictureOfTheDay> getAPOD(@QueryMap Map<String, String> options);
}
