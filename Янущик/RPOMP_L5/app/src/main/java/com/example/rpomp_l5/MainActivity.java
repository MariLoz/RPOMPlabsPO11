package com.example.rpomp_l5;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private boolean permissionGranted = false;

    private MediaPlayer mPlayer;
    private Button startButton, pauseButton, stopButton;
    private Button videoPlayButton, videoPauseButton, videoStopButton;
    private VideoView videoView;
    private String setType;
    private ImageView imageView;
    private Uri currentVideoUri;
    private TextView myTextView;


    private static final int PICKFILE_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!permissionGranted) {
            checkPermissions();
        }

        startButton = findViewById(R.id.start);
        pauseButton = findViewById(R.id.pause);
        stopButton = findViewById(R.id.stop);
        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);
        // Инициализация кнопок управления видео
        videoPlayButton = findViewById(R.id.videoPlay);
        videoPauseButton = findViewById(R.id.videoPause);
        videoStopButton = findViewById(R.id.videoStop);
        myTextView = findViewById(R.id.myTextView);
    }

    public boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    private boolean checkPermissions() {
        if (!isExternalStorageReadable() || !isExternalStorageWriteable()) {
            Toast.makeText(this, "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "Разрешения получены", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void onClFile(View viewButton) {
        if (viewButton.getId() == R.id.buttonAudio) {
            setType = "audio/*";
        }
        if (viewButton.getId() == R.id.buttonVideo) {
            setType = "video/*";
        }
        if (viewButton.getId() == R.id.buttonImage) {
            setType = "image/*";
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(setType);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            findViewById(R.id.audioControls).setVisibility(View.GONE);
            findViewById(R.id.videoControls).setVisibility(View.GONE); // Скрываем кнопки плеера
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            myTextView.setVisibility(View.GONE);
            if (setType.equals("audio/*")) {
                findViewById(R.id.audioControls).setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                mPlayer = MediaPlayer.create(this, data.getData());
                mPlayer.start();
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlay();
                    }
                });
            }
            if (setType.equals("video/*")) {
                findViewById(R.id.audioControls).setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                findViewById(R.id.videoControls).setVisibility(View.VISIBLE); // Показываем кнопки управления видео
                imageView.setVisibility(View.GONE);
                myTextView.setVisibility(View.VISIBLE);


                // Устанавливаем URI для видео
                currentVideoUri = data.getData(); // Сохраняем URI видео
                videoView.setVideoURI(currentVideoUri);

                // Получаем размеры экрана
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                // Ожидаем, пока видео будет готово к воспроизведению
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // Получаем размеры видео
                        int videoWidth = mp.getVideoWidth();
                        int videoHeight = mp.getVideoHeight();

                        // Рассчитываем соотношение сторон видео
                        float videoAspectRatio = (float) videoWidth / videoHeight;

                        // Рассчитываем доступное пространство на экране
                        int availableWidth = screenWidth;
                        int availableHeight = screenHeight;

                        // Рассчитываем новые размеры VideoView
                        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();

                        if (videoWidth < availableWidth && videoHeight < availableHeight) {
                            // Если видео меньше доступного пространства, растягиваем его пропорционально
                            if (videoAspectRatio > (float) availableWidth / availableHeight) {
                                // Видео шире, чем доступное пространство по ширине
                                layoutParams.width = availableWidth;
                                layoutParams.height = (int) (availableWidth / videoAspectRatio);
                            } else {
                                // Видео выше, чем доступное пространство по высоте
                                layoutParams.height = availableHeight;
                                layoutParams.width = (int) (availableHeight * videoAspectRatio);
                            }
                        } else {
                            // Если видео больше доступного пространства, подгоняем под размеры экрана
                            if (videoAspectRatio > (float) availableWidth / availableHeight) {
                                // Видео шире, чем доступное пространство по ширине
                                layoutParams.width = availableWidth;
                                layoutParams.height = (int) (availableWidth / videoAspectRatio);
                            } else {
                                // Видео выше, чем доступное пространство по высоте
                                layoutParams.height = availableHeight;
                                layoutParams.width = (int) (availableHeight * videoAspectRatio);
                            }
                        }

                        // Применяем новые размеры
                        videoView.setLayoutParams(layoutParams);

                        // Включаем кнопку Play и отключаем Pause/Stop
                        videoPlayButton.setEnabled(true);
                        videoPauseButton.setEnabled(false);
                        videoStopButton.setEnabled(false);
                    }
                });
            }
            if (setType.equals("image/*")) {
                findViewById(R.id.audioControls).setVisibility(View.GONE);
                videoView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageURI(data.getData());
            }
        }
    }

    public void play(View view) {
        mPlayer.start();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    public void pause(View view) {
        mPlayer.pause();
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void stop(View view) {
        stopPlay();
    }

    private void stopPlay() {
        mPlayer.stop();
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        try {
            mPlayer.prepare();
            mPlayer.seekTo(0);
            startButton.setEnabled(true);
        } catch (Throwable t) {
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для воспроизведения видео
    public void onVideoPlay(View view) {
        if (!videoView.isPlaying()) {
            videoView.start(); // Запуск видео
            videoPlayButton.setEnabled(false); // Отключаем кнопку Play
            videoPauseButton.setEnabled(true); // Включаем кнопку Pause
            videoStopButton.setEnabled(true); // Включаем кнопку Stop
        }
    }

    // Метод для паузы видео
    public void onVideoPause(View view) {
        if (videoView.isPlaying()) {
            videoView.pause(); // Пауза видео
            videoPlayButton.setEnabled(true); // Включаем кнопку Play
            videoPauseButton.setEnabled(false); // Отключаем кнопку Pause
        }
    }

    // Метод для остановки видео
    public void onVideoStop(View view) {
        if (videoView.isPlaying()) {
            videoView.stopPlayback(); // Остановка видео
            videoPlayButton.setEnabled(true); // Включаем кнопку Play
            videoPauseButton.setEnabled(false); // Отключаем кнопку Pause
            videoStopButton.setEnabled(false); // Отключаем кнопку Stop

            // Сбрасываем VideoView и готовим его к повторному воспроизведению
            if (currentVideoUri != null) {
                videoView.setVideoURI(currentVideoUri); // Устанавливаем сохраненный URI
            }
        }
    }
}