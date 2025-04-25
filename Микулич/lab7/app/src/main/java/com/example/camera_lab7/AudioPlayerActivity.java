package com.example.camera_lab7;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AudioPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPause;
    private boolean isPaused = false;
    private String audioPath;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        audioPath = getIntent().getStringExtra("audioPath");
        if (audioPath == null) {
            finish();
            return;
        }


        Uri audioUri = Uri.parse(audioPath);

        ImageView audioImage = findViewById(R.id.audioImage);
        TextView textAudioName = findViewById(R.id.textAudioName);
        TextView textAudioSize = findViewById(R.id.textAudioSize);
        TextView textAudioDate = findViewById(R.id.textAudioDate);
        TextView textAudioDuration = findViewById(R.id.textAudioDuration);
        Button btnBack = findViewById(R.id.btnBack);
        btnPause = findViewById(R.id.btnPause);
        seekBar = findViewById(R.id.seekBar2);

        displayAudioInfo(audioUri, textAudioName, textAudioSize, textAudioDate);

        displayAudioDuration(audioUri, textAudioDuration);

        mediaPlayer = MediaPlayer.create(this, audioUri);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            updateSeekBar();
        }

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            seekBar.setProgress(mp.getCurrentPosition());
        });

        btnBack.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            finish();
        });

        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btnPause.setText("Продолжить");
                    isPaused = true;
                } else if (isPaused) {
                    mediaPlayer.start();
                    btnPause.setText("Пауза");
                    isPaused = false;
                    updateSeekBar();
                }
            }
        });
    }

    // Метод для получения информации о файле (Имя, Размер, Дата изменения)
    private void displayAudioInfo(Uri audioUri, TextView textAudioName, TextView textAudioSize, TextView textAudioDate) {
        Cursor cursor = getContentResolver().query(audioUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int dateIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED);

            if (nameIndex != -1) {
                textAudioName.setText("Имя: " + cursor.getString(nameIndex));
            }

            if (sizeIndex != -1) {
                long fileSize = cursor.getLong(sizeIndex);
                textAudioSize.setText("Размер: " + fileSize / 1024 + " KB");
            }

            String lastModified = getFileLastModified(audioUri);
            textAudioDate.setText("Дата изменения: " + lastModified);

            cursor.close();
        }
    }

    // Метод для получения длительности аудиофайла
    private void displayAudioDuration(Uri audioUri, TextView textAudioDuration) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, audioUri);

            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (durationStr != null) {
                long durationMs = Long.parseLong(durationStr);
                int seconds = (int) (durationMs / 1000) % 60;
                int minutes = (int) ((durationMs / (1000 * 60)) % 60);
                textAudioDuration.setText(String.format("Длительность: %02d:%02d", minutes, seconds));
            }
        } catch (Exception e) {
            Log.e("AudioMetadata", "Ошибка при обработке аудиофайла", e);
            textAudioDuration.setText("Ошибка: " + e.getMessage());
        } finally {
            try {
                retriever.release();
            } catch (Exception e) {
                Log.e("AudioMetadata", "Ошибка при освобождении ресурса retriever", e);
            }
        }
    }

    // Получаем дату последнего изменения файла
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 0);
                }
            }
        }, 500);
    }

}
