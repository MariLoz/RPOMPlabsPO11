package com.example.camera_lab7;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import android.media.ExifInterface; // Добавить этот импорт
import java.io.IOException;


public class GalleryActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnNext, btnPrev, btnDelete, btnHome;
    private List<File> images = new ArrayList<>();
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imageView = findViewById(R.id.imageView);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        btnDelete = findViewById(R.id.btnDelete);
        btnHome = findViewById(R.id.btnHome);

        loadImages();

        if (!images.isEmpty()) {
            displayImage();
        } else {
            Toast.makeText(this, "Галерея пуста", Toast.LENGTH_SHORT).show();
        }

        btnNext.setOnClickListener(v -> {
            if (currentIndex < images.size() - 1) {
                currentIndex++;
                displayImage();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayImage();
            }
        });

        btnDelete.setOnClickListener(v -> deleteImage());

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadImages() {
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (directory.exists()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".jpg"));
            if (files != null) {
                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                images = new ArrayList<>(Arrays.asList(files));
            }
        }
    }



    private void deleteImage() {
        if (!images.isEmpty()) {
            File fileToDelete = images.get(currentIndex);
            if (fileToDelete.delete()) {
                images.remove(currentIndex);
                if (images.isEmpty()) {
                    imageView.setImageResource(android.R.color.darker_gray);
                    Toast.makeText(this, "Галерея пуста", Toast.LENGTH_SHORT).show();
                } else {
                    currentIndex = Math.max(0, currentIndex - 1);
                    displayImage();
                }
            } else {
                Toast.makeText(this, "Ошибка удаления", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayImage() {
        if (!images.isEmpty()) {
            String imagePath = images.get(currentIndex).getAbsolutePath();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                bitmap = rotateImageIfRequired(bitmap, imagePath); // Исправляем ориентацию
                imageView.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, String path) {
        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return img;
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
