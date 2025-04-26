package com.example.kozinlab7;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AudioActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBarAudio;
    private Button btnPlayPauseAudio, btnBackAudio;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        seekBarAudio = findViewById(R.id.seekBarAudio);
        btnPlayPauseAudio = findViewById(R.id.btnPlayPauseAudio);
        btnBackAudio = findViewById(R.id.btnBackAudio);

        Uri audioUri = getIntent().getData();
        mediaPlayer = MediaPlayer.create(this, audioUri);

        // Установка слушателей для кнопок
        btnPlayPauseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pauseAudio();
                } else {
                    playAudio();
                }
            }
        });

        btnBackAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем активность и возвращаемся на предыдущий экран
            }
        });

        // Обработка перемотки аудио
        seekBarAudio.setMax(mediaPlayer.getDuration());
        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
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

    private void playAudio() {
        mediaPlayer.start();
        isPlaying = true;
        btnPlayPauseAudio.setText("Пауза");
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        btnPlayPauseAudio.setText("Воспроизвести");
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBarAudio.setProgress(currentPosition);
                }
                handler.postDelayed(this, 1000); // Обновляем SeekBar каждую секунду
            }
        }, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}