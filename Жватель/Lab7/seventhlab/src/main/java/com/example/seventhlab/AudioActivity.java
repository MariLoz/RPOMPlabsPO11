package com.example.seventhlab;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AudioActivity extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 1;
    private MediaPlayer mPlayer;
    private Button startButton, pauseButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);
        Button selectAudioButton = findViewById(R.id.selectAudioButton);

        selectAudioButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        });

        startButton.setOnClickListener(v -> play());
        pauseButton.setOnClickListener(v -> pause());
        stopButton.setOnClickListener(v -> stop());
    }

    private void processFile(Uri uri) {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = MediaPlayer.create(this, uri);
        if (mPlayer != null) {
            mPlayer.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            mPlayer.setOnCompletionListener(mp -> stop());
        } else {
            Toast.makeText(this, "Failed to load audio file", Toast.LENGTH_SHORT).show();
        }
    }

    private void play() {
        if (mPlayer != null) {
            mPlayer.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }

    private void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    private void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            processFile(data.getData());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}