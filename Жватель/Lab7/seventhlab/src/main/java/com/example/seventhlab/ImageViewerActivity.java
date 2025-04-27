package com.example.seventhlab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ImageViewerActivity extends AppCompatActivity {
    private static final int PICKFILE_RESULT_CODE = 1;
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.imageView);
        Button selectImageButton = findViewById(R.id.selectImageButton);
        Button zoomInButton = findViewById(R.id.zoomInButton);
        Button zoomOutButton = findViewById(R.id.zoomOutButton);

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
        });

        zoomInButton.setOnClickListener(v -> {
            scaleFactor *= 1.2f;
            scaleFactor = Math.min(scaleFactor, 5.0f);
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
        });

        zoomOutButton.setOnClickListener(v -> {
            scaleFactor /= 1.2f;
            scaleFactor = Math.max(scaleFactor, 0.1f);
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
        });

        imageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {
            imageView.setImageURI(data.getData());
            imageView.setVisibility(View.VISIBLE);
            scaleFactor = 1.0f;
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
        }
    }
}