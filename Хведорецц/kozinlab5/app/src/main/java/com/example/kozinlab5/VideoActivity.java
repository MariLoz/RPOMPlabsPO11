package com.example.kozinlab5;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private SeekBar seekBarVideo;
    private Button btnPlayPauseVideo, btnBackVideo;
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

        Uri videoUri = getIntent().getData();
        videoView.setVideoURI(videoUri);


        btnPlayPauseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseVideo();
                } else {
                    playVideo();
                }
            }
        });

        btnBackVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Обработка перемотки видео
        seekBarVideo.setMax(videoView.getDuration());
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

        // Обновление SeekBar
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
                    int currentPosition = videoView.getCurrentPosition();
                    seekBarVideo.setProgress(currentPosition);
                    seekBarVideo.setMax(videoView.getDuration()); // Убедитесь, что максимальное значение обновляется
                }
                handler.postDelayed(this, 1000); // Обновляем SeekBar каждую секунду
            }
        }, 0);
    }


}