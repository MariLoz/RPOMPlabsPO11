package com.example.seventhlab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String HELP_TEXT = "Multi-Window App\n\n" +
            "Features:\n" +
            "- Audio Playback: Play audio files from storage.\n" +
            "- Video Playback: Play video files from storage.\n" +
            "- Photo Capture: Take photos using the device camera.\n" +
            "- Image Viewer: View images with zoom and swipe gestures.\n\n" +
            "Author: Жватель Станислав Сергеевич\n" +
            "Group: ПО-11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button audioButton = findViewById(R.id.audioButton);
        Button videoButton = findViewById(R.id.videoButton);
        Button cameraButton = findViewById(R.id.cameraButton);
        Button imageButton = findViewById(R.id.imageButton);
        Button helpButton = findViewById(R.id.helpButton);

        audioButton.setOnClickListener(v -> startActivity(new Intent(this, AudioActivity.class)));
        videoButton.setOnClickListener(v -> startActivity(new Intent(this, VideoActivity.class)));
        cameraButton.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));
        imageButton.setOnClickListener(v -> startActivity(new Intent(this, ImageViewerActivity.class)));
        helpButton.setOnClickListener(v -> showHelp());
    }

    private void showHelp() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        TextView helpText = popupView.findViewById(R.id.instruction_text);
        helpText.setText(HELP_TEXT);

        Button okButton = popupView.findViewById(R.id.ok_button);
        okButton.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.CENTER, 0, 0);
    }
}