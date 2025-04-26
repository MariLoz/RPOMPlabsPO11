package com.example.media;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;

public class MediaActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private CheckBox chbLoop;
    private SurfaceView surfaceView;
    private float scaleFactor = 1.0f;
    private static final int STORAGE_PERMISSION_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        chbLoop = findViewById(R.id.chb_Loop);
        surfaceView = findViewById(R.id.surfaceView1);
        chbLoop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(isChecked);
            }
        });

        findViewById(R.id.bZoomIn).setOnClickListener(v -> zoom(1.1f));
        findViewById(R.id.bZoomOut).setOnClickListener(v -> zoom(0.9f));
        findViewById(R.id.bBack).setOnClickListener(v -> finish());

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        checkStoragePermissions();
    }

    private void checkStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }
    }

    private void zoom(float factor) {
        scaleFactor *= factor;
        surfaceView.setScaleX(scaleFactor);
        surfaceView.setScaleY(scaleFactor);
    }

    public void onClickStart(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showMessage("Требуется разрешение на чтение хранилища");
            checkStoragePermissions();
            return;
        }

        releaseMP();
        String DATA = ((EditText) findViewById(R.id.et_MediaPath)).getText().toString();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(DATA);
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setLooping(chbLoop.isChecked());
            mediaPlayer.setOnCompletionListener(this);
            HistoryDatabase.getInstance(this).addMediaHistory(DATA);
        } catch (Exception e) {
            showMessage("Ошибка воспроизведения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        if (mediaPlayer == null) return;
        int viewId = view.getId();
        if (viewId == R.id.b_Pause) {
            if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        } else if (viewId == R.id.b_Resume) {
            if (!mediaPlayer.isPlaying()) mediaPlayer.start();
        } else if (viewId == R.id.b_Stop) {
            mediaPlayer.stop();
        }
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showMessage(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        showMessage("Воспроизведение завершено");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMessage("Разрешение на хранилище предоставлено");
            } else {
                showMessage("Разрешение на хранилище отклонено. Некоторые функции могут быть недоступны.");
            }
        }
    }
}