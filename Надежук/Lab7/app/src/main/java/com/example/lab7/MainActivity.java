package com.example.lab7;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> multimediaLauncher;
    private int clickedButtonId = 0;

    @SuppressLint("MissingInflatedId")
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        multimediaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleActivityResult(result)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.lab_info_settings) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleActivityResult(@NonNull ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        launchMultimediaActivity(uri);
                    } catch (SecurityException e) {
                        Toast.makeText(this, "Нет прав доступа к файлу", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "URI файла отсутствует", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Данные отсутствуют", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Выбор файла отменен", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchMultimediaActivity(@NonNull Uri multimediaUri) {
        Intent intent = null;
        if (clickedButtonId == R.id.videoButton){
            intent = new Intent(this, VideoActivity.class);
            intent.putExtra("videoUri", multimediaUri.toString());
        } else if (clickedButtonId == R.id.audioButton) {
            intent = new Intent(this, AudioActivity.class);
            intent.putExtra("audioUri", multimediaUri.toString());
        } else if (clickedButtonId == R.id.imageButton) {
            intent = new Intent(this, ImageActivity.class);
            intent.putExtra("imageUri", multimediaUri.toString());
        }
        startActivity(intent);
    }

    @SuppressLint("IntentReset")
    public void clickOnMultimediaButton(@NonNull View v){
        Intent intent;
        String type;
        if (v.getId() == R.id.videoButton) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            type = "video/*";
        } else if (v.getId() == R.id.audioButton) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            type = "audio/*";
        } else if (v.getId() == R.id.imageButton) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            type = "image/*";
        } else {
            Toast.makeText(this, "Неизвестный тип файла", Toast.LENGTH_SHORT).show();
            return;
        }

        clickedButtonId = v.getId();
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        multimediaLauncher.launch(intent);
    }

    public void clickOnTakePhotoButton(@NonNull View v){
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
}