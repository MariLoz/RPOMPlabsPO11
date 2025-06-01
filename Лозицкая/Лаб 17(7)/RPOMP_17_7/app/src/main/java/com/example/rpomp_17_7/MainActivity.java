package com.example.rpomp_17_7;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnPhoto).setOnClickListener(v ->
                startActivity(new Intent(this, PhotoActivity.class)));

        findViewById(R.id.btnVideo).setOnClickListener(v ->
                startActivity(new Intent(this, VideoActivity.class)));

        findViewById(R.id.btnAudio).setOnClickListener(v ->
                startActivity(new Intent(this, AudioActivity.class)));

        findViewById(R.id.btnMedia).setOnClickListener(v ->
                startActivity(new Intent(this, MediaPlaybackActivity.class)));
    }
}