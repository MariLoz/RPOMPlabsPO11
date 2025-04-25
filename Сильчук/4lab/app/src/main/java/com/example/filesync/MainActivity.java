package com.example.filesync;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int PICK_IMAGE_REQUEST = 3;

    private VideoView videoView;
    private ImageView imageView;
    private MediaPlayer mediaPlayer;
    private Button buttonPlayPause;
    private Button buttonRewind;
    private Button buttonForward;

    private boolean isAudioPlaying = false; // Флаг для отслеживания воспроизведения аудио
    private boolean isVideoPlaying = false; // Флаг для отслеживания воспроизведения видео

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов
        Button buttonShowInstructions = findViewById(R.id.button_show_instructions);
        Button buttonLoadAudio = findViewById(R.id.button_load_audio);
        Button buttonLoadVideo = findViewById(R.id.button_load_video);
        Button buttonLoadImage = findViewById(R.id.button_load_image);
        videoView = findViewById(R.id.video_view);
        imageView = findViewById(R.id.image_view);
        buttonPlayPause = findViewById(R.id.button_play_pause);
        buttonRewind = findViewById(R.id.button_rewind);
        buttonForward = findViewById(R.id.button_forward);

        // Обработчик для кнопки "Показать инструкцию"
        buttonShowInstructions.setOnClickListener(v -> InstructionPopup.showPopup(this, v));

        // Обработчики для кнопок загрузки
        buttonLoadAudio.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_AUDIO_REQUEST);
        });

        buttonLoadVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
        });

        buttonLoadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Обработчики для кнопок управления
        buttonPlayPause.setOnClickListener(v -> togglePlayPause());
        buttonRewind.setOnClickListener(v -> rewind(5000)); // Перемотка на 5 секунд назад
        buttonForward.setOnClickListener(v -> forward(5000)); // Перемотка на 5 секунд вперёд
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();

            switch (requestCode) {
                case PICK_AUDIO_REQUEST:
                    playAudio(selectedFileUri);
                    break;
                case PICK_VIDEO_REQUEST:
                    playVideo(selectedFileUri);
                    break;
                case PICK_IMAGE_REQUEST:
                    displayImage(selectedFileUri);
                    break;
            }
        }
    }

    private void playAudio(Uri audioUri) {
        // Останавливаем и сбрасываем видео, если оно воспроизводится
        if (isVideoPlaying) {
            videoView.stopPlayback(); // Полная остановка видео
            videoView.setVisibility(View.GONE); // Скрываем VideoView
            isVideoPlaying = false;
        }

        // Освобождаем предыдущий MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Инициализируем MediaPlayer
        mediaPlayer = MediaPlayer.create(this, audioUri);
        mediaPlayer.start();
        isAudioPlaying = true;

        // Показываем кнопки управления
        findViewById(R.id.controls_container).setVisibility(View.VISIBLE);
        buttonPlayPause.setText("⏸️"); // Устанавливаем иконку паузы
    }

    private void playVideo(Uri videoUri) {
        // Останавливаем и сбрасываем аудио, если оно воспроизводится
        if (isAudioPlaying) {
            mediaPlayer.release();
            mediaPlayer = null;
            isAudioPlaying = false;
        }

        // Скрываем ImageView
        imageView.setVisibility(View.GONE);

        // Настраиваем VideoView
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.start();
            isVideoPlaying = true;
        });
        videoView.setVisibility(View.VISIBLE);

        // Показываем кнопки управления
        findViewById(R.id.controls_container).setVisibility(View.VISIBLE);
        buttonPlayPause.setText("⏸️"); // Устанавливаем иконку паузы
    }

    private void displayImage(Uri imageUri) {
        // Останавливаем и сбрасываем аудио и видео, если они воспроизводятся
        if (isAudioPlaying) {
            mediaPlayer.release();
            mediaPlayer = null;
            isAudioPlaying = false;
        }
        if (isVideoPlaying) {
            videoView.stopPlayback();
            videoView.setVisibility(View.GONE);
            isVideoPlaying = false;
        }

        // Настраиваем ImageView
        imageView.setImageURI(imageUri);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // Соотношение сторон
        imageView.setVisibility(View.VISIBLE);

        // Скрываем кнопки управления
        findViewById(R.id.controls_container).setVisibility(View.GONE);
    }

    private void togglePlayPause() {
        if (isAudioPlaying) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPlayPause.setText("▶️");
            } else {
                mediaPlayer.start();
                buttonPlayPause.setText("⏸️");
            }
        } else if (isVideoPlaying) {
            if (videoView.isPlaying()) {
                videoView.pause();
                buttonPlayPause.setText("▶️");
            } else {
                videoView.start();
                buttonPlayPause.setText("⏸️");
            }
        }
    }

    private void rewind(int milliseconds) {
        if (isAudioPlaying) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - milliseconds);
        } else if (isVideoPlaying) {
            videoView.seekTo(videoView.getCurrentPosition() - milliseconds);
        }
    }

    private void forward(int milliseconds) {
        if (isAudioPlaying) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + milliseconds);
        } else if (isVideoPlaying) {
            videoView.seekTo(videoView.getCurrentPosition() + milliseconds);
        }
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