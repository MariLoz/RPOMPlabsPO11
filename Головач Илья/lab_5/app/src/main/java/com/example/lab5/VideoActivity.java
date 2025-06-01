package com.example.lab5;

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

        // Настройка слушателей для кнопок
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
                finish(); // Закрываем активность и возвращаемся на предыдущий экран
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

        // Отображение названия файла
        TextView tvNowPlaying = findViewById(R.id.tvNowPlaying); // Добавьте TextView в разметку
        tvNowPlaying.setText("Сейчас воспроизводится: " + getFileName(videoUri));
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


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (displayNameIndex != -1) {
                            result = cursor.getString(displayNameIndex); // Получаем имя файла
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment(); // Если имя не найдено, используем последнюю часть пути
        }
        return result != null ? result : "Неизвестный файл";
    }

}
