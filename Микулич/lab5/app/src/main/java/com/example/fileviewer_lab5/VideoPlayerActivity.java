package com.example.fileviewer_lab5;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView textVideoName, textVideoSize, textVideoDate, textVideoDuration, textVideoResolution;
    private Button btnBack;
    private Button btnPause;
    private boolean isPaused = false;
    private SeekBar seekBar;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        btnPause = findViewById(R.id.btnPause);
        textVideoName = findViewById(R.id.textVideoName);
        textVideoSize = findViewById(R.id.textVideoSize);
        textVideoDate = findViewById(R.id.textVideoDate);
        textVideoDuration = findViewById(R.id.textVideoDuration);
        textVideoResolution = findViewById(R.id.textVideoResolution);
        btnBack = findViewById(R.id.btnBack);
        seekBar = findViewById(R.id.seekBar);

        Uri videoUri = getIntent().getData();
        if (videoUri != null) {
            videoView.setVideoURI(videoUri);
            videoView.start();  // Автоматический запуск видео

            displayVideoInfo(videoUri);
            videoView.setOnPreparedListener(mp -> {
                int duration = videoView.getDuration();
                seekBar.setMax(duration); // Устанавливаем максимальное значение SeekBar
                Log.d("VideoPlayerActivity", "Video duration: " + duration);
                updateSeekBar(); // Запускаем обновление SeekBar
            });

            videoView.setOnCompletionListener(mp -> {
                seekBar.setProgress(0); // Сбрасываем SeekBar по завершению видео
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e("VideoPlayerActivity", "Error occurred: " + what + ", " + extra);
                return true; // обработка ошибки
            });
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
        }

        btnPause.setOnClickListener(view -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnPause.setText("Продолжить");
                isPaused = true;
            } else {
                videoView.start();
                btnPause.setText("Пауза");
                isPaused = false;
                updateSeekBar();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView.isPlaying()) {
                    seekBar.setProgress(videoView.getCurrentPosition());
                    handler.postDelayed(this, 0);
                }
            }
        }, 500);
    }


    private void displayVideoInfo(Uri videoUri) {
        Cursor cursor = getContentResolver().query(videoUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
            int dateIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED);

            if (nameIndex != -1) {
                textVideoName.setText("Имя: " + cursor.getString(nameIndex));
            }

            if (sizeIndex != -1) {
                long fileSize = cursor.getLong(sizeIndex);
                textVideoSize.setText("Размер: " + fileSize / 1024 + " KB");
            }

            String lastModified = getFileLastModified(videoUri);
            textVideoDate.setText("Дата изменения: " + lastModified);

            cursor.close();
        }

        // Получаем метаданные видео (длительность и разрешение)
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, videoUri);

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (durationStr != null) {
                long durationMs = Long.parseLong(durationStr);
                int seconds = (int) (durationMs / 1000) % 60;
                int minutes = (int) ((durationMs / (1000 * 60)) % 60);
                textVideoDuration.setText(String.format("Длительность: %02d:%02d", minutes, seconds));
            }

            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            if (width != null && height != null) {
                textVideoResolution.setText("Разрешение: " + width + "x" + height);
            }
        } catch (Exception e) {
            Log.e("VideoMetadata", "Ошибка при обработке видео", e);
            textVideoDuration.setText("Ошибка: " + e.getMessage());
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                Log.e("VideoMetadata", "Ошибка при освобождении ресурса retriever", e);
            }
        }
    }


    private String getFileLastModified(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        if (documentFile != null) {
            long lastModified = documentFile.lastModified();
            if (lastModified > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                return sdf.format(new Date(lastModified));
            }
        }
        return "Дата изменения недоступна";
    }




}
