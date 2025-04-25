package com.example.lab1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailFragment extends Fragment {

    private TextView nameTextView;
    private TextView capitalTextView;
    private TextView currencyTextView;
    private ImageView flagImageView;


    // Для загрузки изображения
    private ExecutorService executorService; // Для выполнения задач в фоновом потоке
    private Handler mainHandler; // Для обновления UI в главном потоке
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        this.executorService = Executors.newFixedThreadPool(4); // Создаем пул потоков
        this.mainHandler = new Handler(Looper.getMainLooper()); // Инициализируем Handler для главного потока

        // Связываем с визуал
        nameTextView = view.findViewById(R.id.textViewName1);
        capitalTextView = view.findViewById(R.id.textViewCapital1);
        currencyTextView = view.findViewById(R.id.textViewCurrency1);
        flagImageView = view.findViewById(R.id.imageViewFlag1);

        // Получение данных из Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            nameTextView.setText(bundle.getString("name"));
            capitalTextView.setText(bundle.getString("capital"));
            currencyTextView.setText(bundle.getString("currency"));

            // Загрузка изображения флага
            String flagUrl = bundle.getString("flagUrl");
            loadImage(flagImageView, flagUrl);
//            new LoadImageTask().execute(flagUrl);
        }

        return view;
    }

    // Метод для загрузки изображения в фоновом потоке
    private void loadImage(ImageView imageView, String imageUrl) {
        executorService.execute(() -> {
            Bitmap bitmap = loadImageFromUrl(imageUrl);
            mainHandler.post(() -> {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    // изображение-заглушка, если загрузка не удалась
                    imageView.setImageResource(R.drawable.no_country);
                }
            });
        });
    }

    // Метод для загрузки изображения из URL
    private Bitmap loadImageFromUrl(String imageUrl) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Асинхронная задача для загрузки изображения
//    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            String imageUrl = urls[0];
//            Bitmap bitmap = null;
//            try {
//                InputStream inputStream = new URL(imageUrl).openStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (bitmap != null) {
//                flagImageView.setImageBitmap(bitmap);
//            } else {
//                // Установите изображение-заглушку, если загрузка не удалась
//            flagImageView.setImageResource(R.drawable.no_country);
//            }
//        }
//    }
}