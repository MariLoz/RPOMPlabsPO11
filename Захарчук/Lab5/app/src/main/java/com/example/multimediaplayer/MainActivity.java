package com.example.multimediaplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.media.MediaPlayer;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private static final int PICKFILE_RESULT_CODE = 1;
    private static final String PREFS_NAME = "MediaPrefs";
    private static final String PREF_PERMISSION_GRANTED = "permissionGranted";
    private boolean permissionGranted = false;
    private MediaPlayer mPlayer;
    private Button startButton, pauseButton, stopButton, taskInfoButton, authorButton;
    private VideoView videoView;
    private ImageView imageView;
    private String setType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        taskInfoButton = findViewById(R.id.taskInfoButton);
        authorButton = findViewById(R.id.authorButton);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        permissionGranted = prefs.getBoolean(PREF_PERMISSION_GRANTED, false);

        if (!permissionGranted) {
            checkPermissions();
        } else {
            setupButtonListeners();
        }

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
    }

    private boolean checkPermissions() {
        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            Toast.makeText(this, "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION_WRITE);
            return false;
        }
        permissionGranted = true;
        savePermissionState();
        setupButtonListeners();
        return true;
    }

    private boolean isExternalStorageWriteable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private void savePermissionState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_PERMISSION_GRANTED, permissionGranted);
        editor.apply();
    }

    private void setupButtonListeners() {
        startButton.setOnClickListener(v -> play(v));
        pauseButton.setOnClickListener(v -> pause(v));
        stopButton.setOnClickListener(v -> stop(v));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                savePermissionState();
                setupButtonListeners();
                Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClFile(View view) {
        if (!permissionGranted) {
            Toast.makeText(this, "Требуются разрешения для доступа к файлам", Toast.LENGTH_SHORT).show();
            checkPermissions();
            return;
        }

        if (view.getId() == R.id.buttonAudio) setType = "audio/*";
        if (view.getId() == R.id.buttonVideo) setType = "video/*";
        if (view.getId() == R.id.buttonImage) setType = "image/*";

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(setType);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (setType != null) {
                if (setType.equals("audio/*")) {
                    try {
                        if (mPlayer != null) {
                            mPlayer.stop();
                            mPlayer.release();
                        }
                        mPlayer = MediaPlayer.create(this, fileUri);
                        mPlayer.start();
                        startButton.setEnabled(false);
                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);
                        mPlayer.setOnCompletionListener(mp -> stopPlay());
                    } catch (Exception e) {
                        Toast.makeText(this, "Ошибка воспроизведения: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (setType.equals("video/*")) {
                    videoView.setVideoURI(fileUri);
                    videoView.start();
                } else if (setType.equals("image/*")) {
                    Intent intent = new Intent(this, ImageViewActivity.class);
                    intent.setData(fileUri);
                    startActivity(intent);
                }
            }
        }
    }

    public void play(View view) {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }

    public void pause(View view) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    public void stop(View view) {
        if (mPlayer != null) stopPlay();
    }

    private void stopPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            try {
                mPlayer.prepare();
                mPlayer.seekTo(0);
                startButton.setEnabled(true);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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