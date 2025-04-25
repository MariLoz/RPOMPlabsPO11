package com.example.lab7;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VideoActivity extends AppCompatActivity {
    private VideoView videoView;
    private ImageButton playButton, pauseButton, stopButton;
    private SeekBar videoProgressBar;
    private TextView videoCurrentTime, videoTotalTime;
    private final Handler mHandler = new Handler();
    private boolean isUserChangingSeekBar = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainVideo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoView = findViewById(R.id.videoView);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        Button backButton = findViewById(R.id.backVideoButton);
        videoProgressBar = findViewById(R.id.videoProgressBar);
        videoCurrentTime = findViewById(R.id.videoCurrentTime);
        videoTotalTime = findViewById(R.id.videoTotalTime);

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        pauseButton.setAlpha(0.3f);
        stopButton.setAlpha(0.3f);

        String uriString = getIntent().getStringExtra("videoUri");
        if (uriString != null) {
            Uri videoUri = Uri.parse(uriString);
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                videoProgressBar.setMax(videoView.getDuration());
                updateVideoProgress();
                updateTotalTime();
            });
        } else {
            Toast.makeText(this, "Строка videoUri пустая", Toast.LENGTH_SHORT).show();
        }

        videoProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                    updateCurrentTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserChangingSeekBar = true;
                mHandler.removeCallbacks(updateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isUserChangingSeekBar = false;
                updateVideoProgress();
            }
        });

        playButton.setOnClickListener(v -> {
            videoView.start();
            playButton.setEnabled(false);
            playButton.setAlpha(0.3f);
            pauseButton.setEnabled(true);
            pauseButton.setAlpha(1.0f);
            stopButton.setEnabled(true);
            stopButton.setAlpha(1.0f);
            updateVideoProgress();
        });

        pauseButton.setOnClickListener(v -> {
            videoView.pause();
            playButton.setEnabled(true);
            playButton.setAlpha(1.0f);
            pauseButton.setEnabled(false);
            pauseButton.setAlpha(0.3f);
            stopButton.setEnabled(true);
            stopButton.setAlpha(1.0f);
            mHandler.removeCallbacks(updateTimeTask);
        });

        stopButton.setOnClickListener(v -> {
            videoView.seekTo(0);
            videoView.pause();
            playButton.setEnabled(true);
            playButton.setAlpha(1.0f);
            pauseButton.setEnabled(false);
            pauseButton.setAlpha(0.3f);
            stopButton.setEnabled(false);
            stopButton.setAlpha(0.3f);
            mHandler.removeCallbacks(updateTimeTask);
            videoProgressBar.setProgress(0);
            updateCurrentTime();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateVideoProgress() {
        mHandler.postDelayed(updateTimeTask, 100);
    }

    private final Runnable updateTimeTask = new Runnable() {
        public void run() {
            if (videoView != null && videoView.isPlaying() && !isUserChangingSeekBar) {
                updateCurrentTime();
                videoProgressBar.setProgress(videoView.getCurrentPosition());
            }
            mHandler.postDelayed(this, 100);
        }
    };

    @SuppressLint("DefaultLocale")
    private void updateCurrentTime() {
        int currentPosition = videoView.getCurrentPosition();
        videoCurrentTime.setText(String.format("%02d:%02d", (currentPosition / 1000) / 60, (currentPosition / 1000) % 60));
    }

    @SuppressLint("DefaultLocale")
    private void updateTotalTime() {
        int totalDuration = videoView.getDuration();
        videoTotalTime.setText(String.format("%02d:%02d", (totalDuration / 1000) / 60, (totalDuration / 1000) % 60));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(updateTimeTask);
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}