package com.example.mediahandler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.VideoView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class MainActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ImageView imageView;
    private VideoView videoView;
    private LinearLayout audioControls;
    private SeekBar seekBar;
    private Button btnStop;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSelectFile = findViewById(R.id.btnSelectFile);
        ImageButton btnInfo = findViewById(R.id.btnInfo);
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        audioControls = findViewById(R.id.audioControls);
        seekBar = findViewById(R.id.seekBar);
        btnStop = findViewById(R.id.btnStop);

        // Инициализация ActivityResultLauncher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            String mimeType = getContentResolver().getType(uri);
                            if (mimeType != null) {
                                handleFile(uri, mimeType);
                            }
                        }
                    }
                }
        );

        btnSelectFile.setOnClickListener(v -> openFilePicker());

        // Обработчик для SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Обработчик для кнопки "Стоп"
        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                audioControls.setVisibility(LinearLayout.GONE);
            }
        });

        // Обработчик для кнопки с иконкой
        btnInfo.setOnClickListener(v -> showInfoDialog());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private void handleFile(Uri uri, String mimeType) {
        if (mimeType.startsWith("image/")) {
            imageView.setVisibility(ImageView.VISIBLE);
            videoView.setVisibility(VideoView.GONE);
            audioControls.setVisibility(LinearLayout.GONE);
            imageView.setImageURI(uri);
        } else if (mimeType.startsWith("audio/")) {
            imageView.setVisibility(ImageView.GONE);
            videoView.setVisibility(VideoView.GONE);
            audioControls.setVisibility(LinearLayout.VISIBLE);

            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = MediaPlayer.create(this, uri);
            mediaPlayer.start();

            // Устанавливаем максимальное значение SeekBar
            seekBar.setMax(mediaPlayer.getDuration());

            // Обновляем SeekBar в реальном времени
            updateSeekBar();
        } else if (mimeType.startsWith("video/")) {
            imageView.setVisibility(ImageView.GONE);
            audioControls.setVisibility(LinearLayout.GONE);
            videoView.setVisibility(VideoView.VISIBLE);
            videoView.setVideoURI(uri);
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            videoView.start();
        }
    }

    // Метод для обновления SeekBar
    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());

            if (mediaPlayer.isPlaying()) {
                Runnable runnable = this::updateSeekBar;
                handler.postDelayed(runnable, 1000); // Обновляем каждую секунду
            }
        }
    }

    // Метод для отображения информации
    private void showInfoDialog() {
        String infoText = "Лабораторная работа № 14. Работа с файловой системой. Разрешения.\n\n" +
                "Выполнил: Сильчук Д.А.\n" +
                "Проверил: Козинский А.А.\n\n" +
                "Задание:\n" +
                "1. Создать приложение, обеспечивающее выбор файла во внешнем хранилище с возможностью дальнейшей его обработки в зависимости от расширения:\n" +
                "- графический файл отобразить с использованием элемента ImageView\n" +
                "- аудиофайл воспроизвести с использованием элемента MediaPlayer\n" +
                "- видеофайл воспроизвести с использованием элемента VideoView.\n" +
                "2. Загрузить заранее набор медиафайлов (изображения, аудио, видео) для тестирования.\n" +
                "3. Создать новый проект.\n" +
                "4. Добавить необходимые элементы интерфейса для реализации всех функций, перечисленных в задании. "
                + "Для реализации различных функций можно использовать как дополнительные разметки, так и дополнительные активности.";

        new AlertDialog.Builder(this)
                .setTitle("Информация")
                .setMessage(infoText)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}