package by.rpomp.lab_5;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VideoActivity extends AppCompatActivity {
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = getIntent().getData();
        videoView.setVideoURI(videoUri);
        final FloatingActionButton playPauseButton = findViewById(R.id.playPauseButton);

        playPauseButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playPauseButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                videoView.start();
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                System.out.println("Current position: " + videoView.getCurrentPosition());
                System.out.println("Duration: " + videoView.getDuration());
            }
            isPlaying = !isPlaying;
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (videoView.isPlaying()) {
                    System.out.println("HANDLER!!!");
                }
                handler.postDelayed(this, 100);
            }
        }, 100);

    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
//    private VideoView videoView;
//    private SeekBar seekBarVideo;
//    private FloatingActionButton playPauseButton;
//    private TextView currentTimeTextView, totalTimeTextView;
//    private Handler handler = new Handler();
//    private boolean isPlaying = false;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_video);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.video), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        videoView = findViewById(R.id.videoView);
//        seekBarVideo = findViewById(R.id.seekBarVideo);
//        playPauseButton = findViewById(R.id.playPauseButton);
//        currentTimeTextView = findViewById(R.id.currentTimeTextView);
//        totalTimeTextView = findViewById(R.id.totalTimeTextView);
//
//        Uri videoUri = getIntent().getData();
//        videoView.setVideoURI(videoUri);
//        playPauseButton.setOnClickListener(view -> onPlayPauseButtonClicked());
//        totalTimeTextView.setText(millisecondsToString(videoView.getDuration()));
//        seekBarVideo.setMax(videoView.getDuration());
//        seekBarVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    videoView.seekTo(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

/*        videoView.setOnPreparedListener(mediaPlayer -> {
            seekBarVideo.setMax(videoView.getDuration());
            totalTimeTextView.setText(millisecondsToString(videoView.getDuration()));
            handler.post(updateTimeTask);
        });*/

    //}

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacks(updateTimeTask);
//    }

/*    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoView.isPlaying()) {
                seekBarVideo.setProgress(videoView.getCurrentPosition());
            }
            currentTimeTextView.setText(millisecondsToString(videoView.getCurrentPosition()));
            seekBarVideo.setProgress(videoView.getCurrentPosition());
            handler.postDelayed(this, 100);
        }
    };*/

//    private Runnable updateTimeTask = new Runnable() {
//        @Override
//        public void run() {
//            updateCurrentTime();
//            updateSeekBar();
//        }
//    };
//
//    private void updateCurrentTime() {
//        currentTimeTextView.setText(millisecondsToString(videoView.getCurrentPosition()));
//        seekBarVideo.setProgress(videoView.getCurrentPosition());
//    }
//
//    private void onPlayPauseButtonClicked() {
//        if (isPlaying) {
//            videoView.pause();
//            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
//            handler.removeCallbacks(updateTimeTask);//
//        } else {
//            videoView.start();
//            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
//            //handler.postDelayed(updateSeekBarRunnable, 100);
//            updateSeekBar();//
//        }
//        isPlaying = !isPlaying;
//    }
//
//    private void updateSeekBar() {
//        handler.postDelayed(updateTimeTask, 100);
//    }
//
//    private String millisecondsToString(int milliseconds) {
//        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
//        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
//        return String.format("%02d:%02d", minutes, seconds);
//    }

}
