package com.example.media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    private int currentImage = 0;
    private ArrayList<String> images;
    private ImageView imageView;
    private TextView nameView;
    private float scaleFactor = 1.0f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        findViewById(R.id.bZoomIn).setOnClickListener(v -> zoom(1.1f));
        findViewById(R.id.bZoomOut).setOnClickListener(v -> zoom(0.9f));
        findViewById(R.id.bBack).setOnClickListener(v -> finish());

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        imageView = findViewById(R.id.image);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float x1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float x2 = event.getX();
                        if (x1 - x2 > 100) onNext(null);
                        else if (x2 - x1 > 100) onPrevious(null);
                        break;
                }
                return true;
            }
        });
    }

    private void zoom(float factor) {
        scaleFactor *= factor;
        imageView.setScaleX(scaleFactor);
        imageView.setScaleY(scaleFactor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentImage = 0;
        nameView = findViewById(R.id.imageName);
        images = new ArrayList<>();
        imageView = findViewById(R.id.image);
        try {
            File imagesDirectory = new File(getExternalFilesDir(null), "TrainingMedia");
            if (!imagesDirectory.exists()) {
                imagesDirectory.mkdirs();
            }
            images = searchImage(imagesDirectory);
            if (images.isEmpty()) {
                nameView.setText("Нет изображений в папке TrainingMedia");
            } else {
                updatePhoto(Uri.parse(images.get(currentImage)));
            }
        } catch (Exception e) {
            nameView.setText("Ошибка: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        images.clear();
    }

    private ArrayList<String> searchImage(File dir) {
        ArrayList<String> imagesFinded = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory()) {
                    String fileExt = getFileExt(f.getAbsolutePath());
                    if (fileExt.equals("png") || fileExt.equals("jpg") || fileExt.equals("jpeg")) {
                        imagesFinded.add(f.getAbsolutePath());
                    }
                }
            }
        }
        return imagesFinded;
    }

    public static String getFileExt(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public void updatePhoto(Uri uri) {
        try {
            nameView.setText((currentImage + 1) + "/" + images.size());
            imageView.setImageURI(uri);
        } catch (Exception e) {
            nameView.setText("Ошибка загрузки файла");
        }
    }

    public void onNext(View v) {
        if (currentImage + 1 < images.size() && images.size() > 0) {
            currentImage++;
            updatePhoto(Uri.parse(images.get(currentImage)));
        }
    }

    public void onPrevious(View v) {
        if (currentImage > 0 && images.size() > 0) {
            currentImage--;
            updatePhoto(Uri.parse(images.get(currentImage)));
        }
    }
}