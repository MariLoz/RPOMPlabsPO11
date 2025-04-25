package com.example.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomAdapter extends ArrayAdapter<CountryClass> {
    private Context context;
    private int resource;

    // Для загрузки изображения
    private ExecutorService executorService; // Для выполнения задач в фоновом потоке
    private Handler mainHandler; // Для обновления UI в главном потоке

    public CustomAdapter(Context context, int resource, ArrayList<CountryClass> countries) {
        super(context, resource, countries);
        this.context = context;
        this.resource = resource;

        // Для загрузки изображения
        this.executorService = Executors.newFixedThreadPool(4); // Создаем пул потоков
        this.mainHandler = new Handler(Looper.getMainLooper()); // Инициализируем Handler для главного потока
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Используем convertView для повторного использования уже созданных View
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        // Получаем текущий элемент
        CountryClass country = getItem(position);
        if (country != null) {
            // Инициализация View
            TextView nameTextView = convertView.findViewById(R.id.textViewName);
            TextView capitalTextView = convertView.findViewById(R.id.textViewCapital);
            ImageView flagImageView = convertView.findViewById(R.id.imageView);

            // Установка данных
            if (nameTextView != null) {
                nameTextView.setText(country.getName());
            }
            if (capitalTextView != null) {
                capitalTextView.setText(country.getCapital());
            }


            if (flagImageView != null) {
                loadImage(flagImageView, country.getFlagUrl());

                // Загрузка изображения флага с использованием AsyncTask
//                new LoadImageTask(flagImageView).execute(country.getFlagUrl());
            }
        }

        return convertView;
    }

    // Метод для загрузки изображения в фоновом потоке
    private void loadImage(ImageView imageView, String imageUrl) {
        executorService.execute(() -> {
            Bitmap bitmap = loadImageFromUrl(imageUrl);
            mainHandler.post(() -> {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    // Установите изображение-заглушку, если загрузка не удалась
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
//    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
//        private ImageView imageView;
//
//        public LoadImageTask(ImageView imageView) {
//            this.imageView = imageView;
//        }
//
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
//                imageView.setImageBitmap(bitmap);
//            } else {
//                // Установите изображение-заглушку, если загрузка не удалась
//                imageView.setImageResource(R.drawable.ic_launcher_background);
//            }
//        }
//    }
}