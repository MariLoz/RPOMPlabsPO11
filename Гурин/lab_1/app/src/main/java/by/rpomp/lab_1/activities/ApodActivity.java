package by.rpomp.lab_1.activities;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import by.rpomp.lab_1.MyApplication;
import by.rpomp.lab_1.R;
import by.rpomp.lab_1.api.ApiClient;
import by.rpomp.lab_1.api.ApiService;
import by.rpomp.lab_1.api.AstronomyPictureOfTheDay;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApodActivity extends AppCompatActivity {
    private MyApplication myApplication;
    private ShapeableImageView imageView;
    private TextView apodTitle;
    private TextView apodExplanation;
    private TextView apodDate;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apod);

        myApplication = (MyApplication) getApplicationContext();
        imageView = findViewById(R.id.imageOfTheDay);
        apodTitle = findViewById(R.id.apodTitle);
        apodExplanation = findViewById(R.id.apodExplanation);
        apodDate = findViewById(R.id.apodDate);
        apiService = ApiClient.getClient().create(ApiService.class);

        requestData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.apodActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void requestData() {
        Call<AstronomyPictureOfTheDay> call = apiService.getAPOD(myApplication.getUrlArguments(myApplication.apodUrl));
        call.enqueue(new Callback<AstronomyPictureOfTheDay>() {
            @Override
            public void onResponse(Call<AstronomyPictureOfTheDay> call, Response<AstronomyPictureOfTheDay> response) {
                if (response.isSuccessful()) {
                    AstronomyPictureOfTheDay APODResponse = response.body();
                    display(APODResponse);
                } else {
                    System.out.println(response);
                    Toast.makeText(ApodActivity.this, "Ошибка: нет интернета", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AstronomyPictureOfTheDay> call, Throwable t) {
                System.out.println(t);
                Toast.makeText(ApodActivity.this, "Ошибка: нет интернета", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void display(AstronomyPictureOfTheDay picture) {
        Glide.with(this)
                .load(picture.getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(ApodActivity.this, "Ошибка при загрузке изображения!", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
        apodTitle.setTypeface(null, Typeface.BOLD);
        apodTitle.setText(picture.getTitle());
        apodExplanation.setText(picture.getExplanation());
        apodDate.setText(convertDateFormat(picture.getDate()));
    }

    private String convertDateFormat(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = null;
        try {
            Date parsedDate = inputFormat.parse(date);
            formattedDate = outputFormat.format(Objects.requireNonNull(parsedDate));
        } catch (ParseException e) {
        }
        return formattedDate;
    }
}
