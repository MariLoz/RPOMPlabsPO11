package com.example.lab7;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar seekBarVideo;
    private Button btnPlayPauseVideo, btnBackVideo, btnRewind, btnForward;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.videoView);
        seekBarVideo = findViewById(R.id.seekBarVideo);
        btnPlayPauseVideo = findViewById(R.id.btnPlayPauseVideo);
        btnBackVideo = findViewById(R.id.btnBackVideo);
        btnRewind = findViewById(R.id.btnRewind);
        btnForward = findViewById(R.id.btnForward);

        Uri videoUri = getIntent().getData();
        videoView.setVideoURI(videoUri);

        btnPlayPauseVideo.setOnClickListener(v -> {
            if (isPlaying) {
                pauseVideo();
            } else {
                playVideo();
            }
        });

        btnBackVideo.setOnClickListener(v -> finish());

        btnRewind.setOnClickListener(v -> videoView.seekTo(Math.max(videoView.getCurrentPosition() - 5000, 0)));
        btnForward.setOnClickListener(v -> videoView.seekTo(Math.min(videoView.getCurrentPosition() + 5000, videoView.getDuration())));

        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        updateSeekBar();
    }

    private void playVideo() {
        videoView.start();
        isPlaying = true;
        btnPlayPauseVideo.setText("Пауза");
    }

    private void pauseVideo() {
        videoView.pause();
        isPlaying = false;
        btnPlayPauseVideo.setText("Воспроизвести");
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView != null && videoView.isPlaying()) {
                    seekBarVideo.setProgress(videoView.getCurrentPosition());
                    seekBarVideo.setMax(videoView.getDuration());
                }
                handler.postDelayed(this, 1000);
            }
        }, 0);
    }
}