package com.example.multimediaexplorer;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button nextButton, deleteButton;
    private List<String> imagePaths;
    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        imageView = findViewById(R.id.imageView);
        nextButton = findViewById(R.id.nextButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Загрузка списка изображений
        imagePaths = loadImagesFromStorage();
        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
        } else {
            // Отображаем первое изображение при запуске
            displayImage(currentImageIndex);
        }

        // Кнопка "Next Image"
        nextButton.setOnClickListener(v -> {
            if (!imagePaths.isEmpty()) {
                currentImageIndex = (currentImageIndex + 1) % imagePaths.size();
                displayImage(currentImageIndex);
            }
        });

        // Кнопка "Delete Image"
        deleteButton.setOnClickListener(v -> {
            if (!imagePaths.isEmpty()) {
                deleteImage(currentImageIndex);
            }
        });
    }

    private List<String> loadImagesFromStorage() {
        List<String> paths = new ArrayList<>();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            File[] files = storageDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {
                        paths.add(file.getAbsolutePath());
                    }
                }
            }
        }
        return paths;
    }

    private void displayImage(int index) {
        if (index >= 0 && index < imagePaths.size()) {
            String imagePath = imagePaths.get(index);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    private void deleteImage(int index) {
        if (index >= 0 && index < imagePaths.size()) {
            File imageFile = new File(imagePaths.get(index));
            if (imageFile.delete()) {
                imagePaths.remove(index); // Удаляем изображение из списка
                if (imagePaths.isEmpty()) {
                    Toast.makeText(this, "No images left", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(null); // Очищаем ImageView
                } else {
                    // После удаления показываем предыдущее изображение
                    currentImageIndex = (currentImageIndex - 1 + imagePaths.size()) % imagePaths.size();
                    displayImage(currentImageIndex);
                }
            } else {
                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}