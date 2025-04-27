package com.example.fifthlab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    // Существующие поля остаются без изменений
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted;
    private MediaPlayer mPlayer;
    private Button startButton, pauseButton, stopButton;
    private VideoView videoView;
    private ImageView imageView;
    private String setType;
    private Uri currentVideoUri;
    private LinearLayout storageOptions, sampleOptions;
    private static final int PICKFILE_RESULT_CODE = 1;

    // Добавляем новые поля для информации о задании
    private Button infoButton;
    private static final String TASK_DESCRIPTION = "Лабораторная работа № 14.\n" +
            "Задание:\n" +
            "1. Создать приложение, обеспечивающее выбор файла во внешнем хранилище с возможностью дальнейшей его обработки в зависимости от расширения:\n" +
            "- графический файл отобразить с использованием элемента ImageView\n" +
            "- аудиофайл воспроизвести с использованием элемента MediaPlayer\n" +
            "- видеофайл воспроизвести с использованием элемента VideoView\n" +
            "2. Загрузить заранее набор медиафайлов (изображения, аудио, видео) для тестирования.\n" +
            "3. Создать новый проект.\n" +
            "4. Добавить необходимые элементы интерфейса для реализации всех функций. " +
            "Для реализации различных функций можно использовать как дополнительные разметки, так и дополнительные активности. " +
            "Простейший вариант оформления интерфейса приложения приведен в описании работы.";
    private static final String AUTHOR_INFO = "Выполнил: Жватель Станислав Сергеевич\n" +
            "Группа: ПО-11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!permissionGranted) {
            checkPermissions();
        }

        // Инициализация существующих элементов
        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        storageOptions = findViewById(R.id.storageOptions);
        sampleOptions = findViewById(R.id.sampleOptions);

        // Инициализация новых элементов
        infoButton = findViewById(R.id.infoButton);
        Button pressButton = findViewById(R.id.pressButton);

        // Обработчики для новых кнопок
        infoButton.setOnClickListener(v -> showTaskInfo());
        pressButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Жватель!", Toast.LENGTH_SHORT).show());
    }

    // Метод для отображения информации о задании
    private void showTaskInfo() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        TextView instructionText = popupView.findViewById(R.id.instruction_text);
        instructionText.setText(TASK_DESCRIPTION + "\n\n" + AUTHOR_INFO);

        Button okButton = popupView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(findViewById(R.id.imageView), Gravity.CENTER, 0, 0);
    }
    // Check if external storage is readable
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    // Check if external storage is writable
    public boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Check and request permissions
    private boolean checkPermissions() {
        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            Toast.makeText(this, "External storage not available", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permissions required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    // Toggle visibility of external storage options
    public void toggleStorageOptions(View view) {
        if (storageOptions.getVisibility() == View.VISIBLE) {
            storageOptions.setVisibility(View.GONE);
        } else {
            storageOptions.setVisibility(View.VISIBLE);
            sampleOptions.setVisibility(View.GONE); // Hide the other options
        }
    }

    // Toggle visibility of sample file options
    public void toggleSampleOptions(View view) {
        if (sampleOptions.getVisibility() == View.VISIBLE) {
            sampleOptions.setVisibility(View.GONE);
        } else {
            sampleOptions.setVisibility(View.VISIBLE);
            storageOptions.setVisibility(View.GONE); // Hide the other options
        }
    }

    // Reset the UI state (hide ImageView, VideoView, and options, disable buttons)
    private void resetUI() {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        currentVideoUri = null;
        storageOptions.setVisibility(View.GONE);
        sampleOptions.setVisibility(View.GONE);
    }

    // Handle file selection from external storage
    public void onClFile(View viewButton) {
        // Reset the UI and stop any ongoing playback
        resetUI();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }

        // Determine which button was pressed
        if (viewButton.getId() == R.id.buttonAudio) {
            setType = "audio/*";
        } else if (viewButton.getId() == R.id.buttonVideo) {
            setType = "video/*";
        } else if (viewButton.getId() == R.id.buttonImage) {
            setType = "image/*";
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(setType); // Set the file type
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    // Load predefined sample files from res/raw
    public void loadSampleFile(View viewButton) {
        // Reset the UI and stop any ongoing playback
        resetUI();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }

        // Determine which sample file to load
        if (viewButton.getId() == R.id.buttonSampleImage) {
            setType = "image/*";
            Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_image);
            processFile(imageUri);
        } else if (viewButton.getId() == R.id.buttonSampleAudio) {
            setType = "audio/*";
            Uri audioUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_audio);
            processFile(audioUri);
        } else if (viewButton.getId() == R.id.buttonSampleVideo) {
            setType = "video/*";
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample_video);
            processFile(videoUri);
        }
    }

    // Process the selected file (used for both external storage and sample files)
    private void processFile(Uri uri) {
        if (setType.equals("audio/*")) {
            mPlayer = MediaPlayer.create(this, uri);
            if (mPlayer != null) {
                mPlayer.start();
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                mPlayer.setOnCompletionListener(mp -> stopPlay());
            } else {
                Toast.makeText(this, "Failed to load audio file", Toast.LENGTH_SHORT).show();
            }
        } else if (setType.equals("video/*")) {
            videoView.setVisibility(View.VISIBLE);
            currentVideoUri = uri;
            videoView.setVideoURI(currentVideoUri);
            videoView.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            videoView.setOnCompletionListener(mp -> stopPlay());
        } else if (setType.equals("image/*")) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(uri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            processFile(data.getData());
        }
    }

    // Playback controls for both audio and video
    public void play(View view) {
        if (setType.equals("audio/*") && mPlayer != null) {
            mPlayer.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        } else if (setType.equals("video/*") && currentVideoUri != null) {
            videoView.setVideoURI(currentVideoUri);
            videoView.start();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
        }
    }

    public void pause(View view) {
        if (setType.equals("audio/*") && mPlayer != null) {
            mPlayer.pause();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(true);
        } else if (setType.equals("video/*")) {
            videoView.pause();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    public void stop(View view) {
        stopPlay();
    }

    private void stopPlay() {
        if (setType.equals("audio/*") && mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        } else if (setType.equals("video/*")) {
            videoView.stopPlayback();
            currentVideoUri = null;
        }
        resetUI();
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