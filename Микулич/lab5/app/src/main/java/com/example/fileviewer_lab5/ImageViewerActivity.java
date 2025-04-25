package com.example.fileviewer_lab5;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textFilename, textFilesize, textFiledate;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.image_view);
        textFilename = findViewById(R.id.text_filename);
        textFilesize = findViewById(R.id.text_filesize);
        textFiledate = findViewById(R.id.text_filedate);
        btnBack = findViewById(R.id.btn_back);

        // Получаем переданный Uri
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri imageUri = intent.getData();
            displayImage(imageUri);
            displayFileInfo(imageUri);
        }

        btnBack.setOnClickListener(v -> finish());
    }

    private void displayImage(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displayFileInfo(Uri fileUri) {
        Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // Проверяем, есть ли колонка DISPLAY_NAME
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                String fileName = cursor.getString(nameIndex);
                textFilename.setText("Имя: " + fileName);
            } else {
                textFilename.setText("Имя: неизвестно");
            }

            // Проверяем, есть ли колонка SIZE
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (sizeIndex != -1 && !cursor.isNull(sizeIndex)) {
                long fileSize = cursor.getLong(sizeIndex);
                fileSize/= 1000;
                textFilesize.setText("Размер: " + fileSize + " КB");
            } else {
                textFilesize.setText("Размер: неизвестен");
            }

            // Дата изменения недоступна через content://
            textFiledate.setText("Дата изменения: недоступна");

            cursor.close();
        }

        String lastModified = getFileLastModified(fileUri);
        textFiledate.setText("Дата изменения: " + lastModified);
    }


    private String getFileLastModified(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        if (documentFile != null) {
            long lastModified = documentFile.lastModified();
            if (lastModified > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                return sdf.format(new Date(lastModified));
            }
        }
        return "Дата изменения недоступна";
    }

}