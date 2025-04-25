package com.example.multimediaexplorer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton audioButton = findViewById(R.id.audioButton);
        MaterialButton videoButton = findViewById(R.id.videoButton);
        MaterialButton cameraButton = findViewById(R.id.cameraButton);
        MaterialButton galleryButton = findViewById(R.id.galleryButton);
        MaterialButton helpButton = findViewById(R.id.helpButton);

        audioButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AudioPlayerActivity.class)));
        videoButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, VideoPlayerActivity.class)));
        cameraButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CameraActivity.class)));
        galleryButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GalleryActivity.class)));
        helpButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HelpActivity.class)));
    }
}