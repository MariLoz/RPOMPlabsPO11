package com.example.rpomp_17_7;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MediaPlaybackActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 123;

    private VideoView videoView;
    private ImageView imageView;
    private Button btnPlayPause;
    private TextView tvStatus;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_playback);

        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        tvStatus = findViewById(R.id.tvStatus);

        findViewById(R.id.btnChooseFile).setOnClickListener(v -> openFilePicker());

        btnPlayPause.setOnClickListener(v -> handlePlayPause());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimeTypes = {"image/*", "video/*", "audio/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
                handleSelectedFile(uri);
            }
        }
    }

    private void handleSelectedFile(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType == null) return;

        resetMedia();

        if (mimeType.startsWith("image/")) {
            showImage(uri);
        } else if (mimeType.startsWith("video/")) {
            showVideo(uri);
        } else if (mimeType.startsWith("audio/")) {
            showAudio(uri);
        } else {
            tvStatus.setText("Неподдерживаемый формат");
        }
    }

    private void resetMedia() {
        videoView.stopPlayback();
        imageView.setImageBitmap(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void showImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            btnPlayPause.setVisibility(View.GONE);
            tvStatus.setText("Изображение загружено");
        } catch (FileNotFoundException e) {
            tvStatus.setText("Ошибка загрузки");
            e.printStackTrace();
        }
    }

    private void showVideo(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        btnPlayPause.setVisibility(View.VISIBLE);
        btnPlayPause.setText("Play");

        videoView.setOnPreparedListener(mp -> {
            tvStatus.setText("Видео готово");
            btnPlayPause.setEnabled(true);
        });

        videoView.setOnCompletionListener(mp -> btnPlayPause.setText("Play"));
    }

    private void showAudio(Uri uri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                tvStatus.setText("Аудио готово");
                btnPlayPause.setVisibility(View.VISIBLE);
                btnPlayPause.setText("Play");
                btnPlayPause.setEnabled(true);
            });

            mediaPlayer.setOnCompletionListener(mp -> btnPlayPause.setText("Play"));
        } catch (IOException e) {
            tvStatus.setText("Ошибка загрузки");
            e.printStackTrace();
        }
    }

    private void handlePlayPause() {
        if (videoView.getVisibility() == View.VISIBLE) {
            handleVideoPlayback();
        } else if (mediaPlayer != null) {
            handleAudioPlayback();
        }
    }

    private void handleVideoPlayback() {
        if (videoView.isPlaying()) {
            videoView.pause();
            btnPlayPause.setText("Play");
        } else {
            videoView.start();
            btnPlayPause.setText("Pause");
        }
    }

    private void handleAudioPlayback() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setText("Play");
        } else {
            mediaPlayer.start();
            btnPlayPause.setText("Pause");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        videoView.stopPlayback();
    }
}