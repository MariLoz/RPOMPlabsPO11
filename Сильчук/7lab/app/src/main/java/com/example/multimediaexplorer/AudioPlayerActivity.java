package com.example.multimediaexplorer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class AudioPlayerActivity extends AppCompatActivity {
    private static final int PICK_AUDIO_REQUEST = 1;
    private MediaPlayer mediaPlayer;
    private Button playButton, pauseButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);

        // Запуск Intent для выбора аудиофайла
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);

        playButton.setOnClickListener(v -> {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        });

        stopButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            playAudio(audioUri);
        }
    }

    private void playAudio(Uri audioUri) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, audioUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}