package com.example.fileviewer_lab5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Button btnSelectFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSelectFile = findViewById(R.id.btn_select_file);

        btnSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });


    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // любой файл
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Выберите файл"), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String fileType = getContentResolver().getType(selectedFileUri);
                if (fileType != null && fileType.startsWith("image/")){
                    Intent intent = new Intent(this, ImageViewerActivity.class);
                    intent.setData(selectedFileUri);
                    startActivity(intent);
                }else if (fileType.startsWith("video/")) {
                    Intent intent = new Intent(this, VideoPlayerActivity.class);
                    intent.setData(selectedFileUri);
                    startActivity(intent);
                }
                else if (fileType.startsWith("audio/")) {
                    Intent intent = new Intent(this, AudioPlayerActivity.class);
                    intent.setData(selectedFileUri);
                    intent.putExtra("audioPath", selectedFileUri.toString()); // Передаем путь к файлу
                    startActivity(intent);
                }
            }
        }
    }
}