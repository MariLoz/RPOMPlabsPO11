package com.example.lab5;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnChooseFile = findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            String mimeType = getContentResolver().getType(fileUri);

            if (mimeType != null) {
                Intent intent;
                switch (mimeType) {
                    case "image/jpeg": // Для изображений
                    case "image/png":
                        intent = new Intent(this, ImageActivity.class);
                        break;
                    case "audio/mpeg": // Для аудио
                    case "audio/mp3": // (дополнительный MIME-тип для MP3)
                        intent = new Intent(this, AudioActivity.class);
                        break;
                    case "video/mp4": // Для видео
                        intent = new Intent(this, VideoActivity.class);
                        break;
                    default:
                        Toast.makeText(this, "Тип файла не поддерживается", Toast.LENGTH_SHORT).show();
                        return;
                }
                intent.setData(fileUri);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Не удалось определить тип файла", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
