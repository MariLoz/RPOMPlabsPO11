package com.example.lab5;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class AudioActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    ImageButton playButton, pauseButton, stopButton;
    private Uri audioUri;
    SeekBar volumeControl, songProgressBar;
    AudioManager audioManager;
    private VolumeChangeReceiver volumeReceiver;
    private TextView nowPlayingText, songCurrentDurationLabel, songTotalDurationLabel;
    private final Handler mHandler = new Handler();
    private boolean isUserChangingSeekBar = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAudio), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        volumeControl = findViewById(R.id.volumeControl);
        songProgressBar = findViewById(R.id.songProgressBar);
        nowPlayingText = findViewById(R.id.nowPlayingText);
        songCurrentDurationLabel = findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = findViewById(R.id.songTotalDurationLabel);

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        pauseButton.setAlpha(0.3f);
        stopButton.setAlpha(0.3f);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeControl.setMax(maxVolume);
        volumeControl.setProgress(currentVolume);
        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        String uriString = getIntent().getStringExtra("audioUri");
        if (uriString != null) {
            audioUri = Uri.parse(uriString);
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(mp -> stopPlay());

                songProgressBar.setMax(mediaPlayer.getDuration());
                updateSongProgress();
                updateNowPlayingText(audioUri);
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка загрузки аудио", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "URI аудио пустое", Toast.LENGTH_SHORT).show();
        }

        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    isUserChangingSeekBar = true;
                    mediaPlayer.seekTo(progress);
                    updateSongProgressLabels();
                    isUserChangingSeekBar = false;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateSongProgress();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    playButton.setEnabled(false);
                    playButton.setAlpha(0.3f);
                    pauseButton.setEnabled(true);
                    pauseButton.setAlpha(1.0f);
                    stopButton.setEnabled(true);
                    stopButton.setAlpha(1.0f);
                    updateSongProgress();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playButton.setEnabled(true);
                    playButton.setAlpha(1.0f);
                    pauseButton.setEnabled(false);
                    pauseButton.setAlpha(0.3f);
                    stopButton.setEnabled(true);
                    stopButton.setAlpha(1.0f);
                    mHandler.removeCallbacks(mUpdateTimeTask);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });

        volumeReceiver = new VolumeChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(volumeReceiver, filter);
    }

    private void stopPlay() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(this, audioUri);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка при подготовке после остановки аудио", Toast.LENGTH_SHORT).show();
            }

            playButton.setEnabled(true);
            playButton.setAlpha(1.0f);
            pauseButton.setEnabled(false);
            pauseButton.setAlpha(0.3f);
            stopButton.setEnabled(false);
            stopButton.setAlpha(0.3f);

            mHandler.removeCallbacks(mUpdateTimeTask);
            songProgressBar.setProgress(0);
            updateSongProgressLabels();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopPlay();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(volumeReceiver);
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private void updateNowPlayingText(Uri uri) {
        String fileName = getFileNameFromUri(uri);
        nowPlayingText.setText(fileName);
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex);
                }
            }
        } catch (Exception e) {
            Log.e("AudioActivity", "Ошибка при получении имени файла из URI", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (fileName == null) {
            fileName = "Неизвестный файл";
        }
        return fileName;
    }

    public void updateSongProgress() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying() && !isUserChangingSeekBar) {
                int currentDuration = mediaPlayer.getCurrentPosition();
                songProgressBar.setProgress(currentDuration);
                updateSongProgressLabels();
            }
            mHandler.postDelayed(this, 100);
        }
    };

    @SuppressLint("SetTextI18n")
    private void updateSongProgressLabels() {
        if (mediaPlayer != null) {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();
            songTotalDurationLabel.setText(" " + milliSecondsToTimer(totalDuration));
            songCurrentDurationLabel.setText(" " + milliSecondsToTimer(currentDuration));
        } else {
            songTotalDurationLabel.setText("0:00");
            songCurrentDurationLabel.setText("0:00");
        }
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    private class VolumeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
                int newVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeControl.setProgress(newVolume);
            }
        }
    }
}