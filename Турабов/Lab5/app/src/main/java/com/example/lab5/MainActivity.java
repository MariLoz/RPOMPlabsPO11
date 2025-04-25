package com.example.lab5;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 100;
    private static final int REQUEST_CODE = 101;

    ImageView imageView;
    MediaPlayer mediaPlayer;
    VideoView videoView;

    private SeekBar seekBar;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;


    TextView name;

    LinearLayout buttonLayout;
    private Button playButton, pauseButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLayout = findViewById(R.id.button_layout);

        playButton= findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        name = findViewById(R.id.audio_name);


        seekBar = findViewById(R.id.seekBar);

        hideButtons();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }

        Button pick = findViewById(R.id.pick_button);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mediaPlayer.stop();
                if(mediaPlayer!= null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                if(videoView != null)
                    videoView.setVisibility(View.GONE);
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });




//        videoPlayer = findViewById(R.id.videoPlayer);
//        Uri myVideoUri= Uri.parse( "android.resource://" + getPackageName() + "/" + R.raw.cats);
//        videoPlayer.setVideoURI(myVideoUri);
//        MediaController mediaController = new MediaController(this);
//        videoPlayer.setMediaController(mediaController);
//        mediaController.setMediaPlayer(videoPlayer);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String mimeType = getContentResolver().getType(uri);
                displayFile(uri, mimeType);
            }
        }
    }

    private void displayFile(Uri uri, String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                displayImage(uri);
            } else if (mimeType.startsWith("video/")) {
                displayVideo(uri);
            } else if (mimeType.startsWith("audio/")) {
                displayAudio(uri);
            } else {
                Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayImage(Uri uri) {
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageURI(uri);
        imageView.setVisibility(View.VISIBLE);

        hideButtons();
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.GONE);
        name.setText("");
    }

    private void displayVideo(Uri uri) {
        showButtons();
        videoView = findViewById(R.id.videoView);
            videoView.setVideoURI(uri);
            videoView.setVisibility(View.VISIBLE);

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);
            name.setText("");

            playButton.setOnClickListener(v -> videoView.start());
            pauseButton.setOnClickListener(v -> videoView.pause());

            videoView.start();

            seekBar.setMax(videoView.getDuration());

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        videoView.seekTo(progress);

                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

        videoView.setOnPreparedListener(mp -> {
            seekBar.setMax(videoView.getDuration());
        });

            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    //if (videoView.isPlaying()) {
                        seekBar.setProgress(videoView.getCurrentPosition());
                        handler.postDelayed(this, 50); // Update every second
                    //}
                }
            };
            updateSeekBar.run();


    }

    private void displayAudio(Uri uri) {
        showButtons();

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();

            String audioName = getAudioName(uri);

            name.setText(audioName);

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);
            videoView = findViewById(R.id.videoView);
            videoView.setVisibility(View.GONE);

            playButton.setOnClickListener(v -> mediaPlayer.start());
            pauseButton.setOnClickListener(v -> mediaPlayer.pause());


            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser&&mediaPlayer != null) {
                        mediaPlayer.seekTo(progress);

                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });

            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer.isPlaying()) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        handler.postDelayed(this, 50); // Update every second
                    }
                }
            };
            updateSeekBar.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAudioName(Uri uri) {
        String displayName = null;
        Cursor cursor = null;
        try {
            // Query the content resolver for the file's display name
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                // Get the column index for the display name
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    displayName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return displayName;
    }

    private void showButtons() {
        if (buttonLayout != null) {
            buttonLayout.setVisibility(View.VISIBLE); // Show the buttons
        }
    }

    private void hideButtons() {
        if (buttonLayout != null) {
            buttonLayout.setVisibility(View.GONE); // Hide the buttons
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }

}