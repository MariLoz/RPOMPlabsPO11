package by.rpomp.lab_7;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;

    //private FloatingActionButton playPauseButton;
    private Uri videoUri;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.video), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoView = findViewById(R.id.videoView);

        //playPauseButton = findViewById(R.id.playPauseButton);

        videoUri = getIntent().getData();
        videoView.setVideoURI(videoUri);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mp -> {
            int duration = videoView.getDuration();

            videoView.start();
            //playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            updateSeekBar();
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e("VideoView", "Error occurred while playing video");
            return false;
        });

/*        playPauseButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                handler.removeCallbacks(updateTime); // Остановить обновление
            } else {
                videoView.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        });*/


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private String getTimeString(int millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private Runnable updateTime = new Runnable() {
        public void run() {
            if (videoView.isPlaying()) {
                int currentPosition = videoView.getCurrentPosition();
            }
            handler.postDelayed(this, 100);
        }
    };

    private void updateSeekBar() {
        handler.postDelayed(updateTime, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTime);
        videoView.pause();
        //playPauseButton.setImageResource(android.R.drawable.ic_media_play);
    }
}

