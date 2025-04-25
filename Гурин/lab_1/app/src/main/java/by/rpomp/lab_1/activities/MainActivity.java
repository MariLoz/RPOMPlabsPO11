package by.rpomp.lab_1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import by.rpomp.lab_1.MyApplication;
import by.rpomp.lab_1.MyArrayAdapter;
import by.rpomp.lab_1.R;
import by.rpomp.lab_1.api.ApiClient;
import by.rpomp.lab_1.api.ApiService;
import by.rpomp.lab_1.api.Photo;
import by.rpomp.lab_1.api.PhotoResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MyApplication myApplication;
    private ListView imageList;
    private ApiService apiService;
    private Button settingsButton;
    private Button apodButton;
    private Button requestButton;
    private Button saveToFileButton;
    private Button sendButton;
    private List<Photo> marsPhotos;
    private MyArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        myApplication = (MyApplication) getApplicationContext();
        apiService = ApiClient.getClient().create(ApiService.class);
        imageList = findViewById(R.id.listView);
        settingsButton = findViewById(R.id.settingsButton);
        apodButton = findViewById(R.id.pictureOfTheDay);
        requestButton = findViewById(R.id.requestButton);
        saveToFileButton = findViewById(R.id.SaveToFileButton);
        sendButton = findViewById(R.id.SendButton);

        marsPhotos = new ArrayList<>();
        adapter = new MyArrayAdapter(this, marsPhotos);

        imageList.setAdapter(adapter);
        imageList.setOnItemClickListener(this::onImageListItemClick);
        settingsButton.setOnClickListener(view -> onClickSettingsButton());
        apodButton.setOnClickListener(view -> onClickApodButton());
        requestButton.setOnClickListener(view -> onClickRequestButton());
        saveToFileButton.setOnClickListener(view -> {
            FileOutputStream fos = null;
            try {
                fos = view.getContext().openFileOutput("marsPhotos.txt", view.getContext().MODE_PRIVATE);
                fos.write(marsPhotos.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            marsPhotos.toString();
        });
        sendButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Данные о фотографиях");
            intent.putExtra(Intent.EXTRA_TEXT, marsPhotos.toString());
            startActivity(Intent.createChooser(intent, "Отправить данные"));
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void onImageListItemClick(AdapterView<?> parent, View view, int position, long id) {
        Photo photo = marsPhotos.get(position);
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("PHOTO_ID", String.valueOf(photo.getId()));
        intent.putExtra("SOL", String.valueOf(photo.getSol()));
        intent.putExtra("ROVER_NAME", photo.getRover().getName());
        intent.putExtra("ROVER_LANDING_DATE", photo.getRover().getLanding_date());
        intent.putExtra("ROVER_LAUNCH_DATE", photo.getRover().getLaunch_date());
        intent.putExtra("ROVER_STATUS", photo.getRover().getStatus());
        intent.putExtra("CAMERA_FULL_NAME", photo.getCamera().getFullName());
        intent.putExtra("IMAGE_URL", photo.getImgSrc());
        startActivity(intent);
    }
    private void onClickSettingsButton() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void onClickApodButton() {
        Intent intent = new Intent(MainActivity.this, ApodActivity.class);
        startActivity(intent);
    }
    private void onClickRequestButton() {
        Call<PhotoResponse> call = apiService.getPhotos(myApplication.getUrlArguments(myApplication.mrpUrl));
        call.enqueue(new Callback<PhotoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PhotoResponse> call, @NonNull Response<PhotoResponse> response) {
                if (response.isSuccessful()) {
                    List<Photo> photos = Objects.requireNonNull(response.body()).getPhotos();
                    marsPhotos.clear();
                    marsPhotos.addAll(photos);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PhotoResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Ошибка: нет интернета", Toast.LENGTH_SHORT).show();
            }
        });
    }
}