package by.rpomp.lab_7;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

public class AudioActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private SeekBar seekBarAudio;
    private FloatingActionButton playPauseButton;
    private TextView currentTimeTextView, totalTimeTextView;
    private Handler handler = new Handler();
    private boolean isPlaying = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_audio);

        seekBarAudio = findViewById(R.id.seekBarAudio);
        playPauseButton = findViewById(R.id.playPauseButton);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        totalTimeTextView = findViewById(R.id.totalTimeTextView);

        Uri audioUri = getIntent().getData();
        mediaPlayer = MediaPlayer.create(this, audioUri);

        playPauseButton.setOnClickListener(view -> onClickPlayPauseButton());
        totalTimeTextView.setText(millisecondsToString(mediaPlayer.getDuration()));
        seekBarAudio.setMax(mediaPlayer.getDuration());
        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    updateCurrentTime();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.audio), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void onClickPlayPauseButton() {
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            handler.removeCallbacks(updateTimeTask);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            updateSeekBar();
        }
        isPlaying = !isPlaying;
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            updateCurrentTime();
            updateSeekBar();
        }
    };

    private void updateCurrentTime() {
        currentTimeTextView.setText(millisecondsToString(mediaPlayer.getCurrentPosition()));
        seekBarAudio.setProgress(mediaPlayer.getCurrentPosition());
    }

    private void updateSeekBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }
}

