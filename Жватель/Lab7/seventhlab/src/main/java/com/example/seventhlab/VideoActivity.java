package com.example.seventhlab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 1;
    private VideoView videoView;
    private Button startButton, pauseButton, stopButton;
    private SeekBar seekBar;
    private Uri currentVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);
        seekBar = findViewById(R.id.seekBar);
        Button selectVideoButton = findViewById(R.id.selectVideoButton);

        selectVideoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        });

        startButton.setOnClickListener(v -> play());
        pauseButton.setOnClickListener(v -> pause());
        stopButton.setOnClickListener(v -> stop());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Update SeekBar progress
        new Thread(() -> {
            while (videoView != null) {
                try {
                    if (videoView.isPlaying()) {
                        runOnUiThread(() -> {
                            seekBar.setMax(videoView.getDuration());
                            seekBar.setProgress(videoView.getCurrentPosition());
                        });
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processFile(Uri uri) {
        videoView.setVisibility(View.VISIBLE);
        currentVideoUri = uri;
        videoView.setVideoURI(currentVideoUri);
        videoView.start();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
        videoView.setOnCompletionListener(mp -> stop());
    }

    private void play() {
        if (currentVideoUri != null) {
            videoView.setVideoURI(currentVideoUri);
            videoView.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }

    private void pause() {
        videoView.pause();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stop() {
        videoView.stopPlayback();
        currentVideoUri = null;
        videoView.setVisibility(View.GONE);
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
}