package com.example.lab7;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button btnPrevious, btnNext, btnRotateLeft, btnRotateRight;
    private List<Uri> imageUris;
    private int currentPosition = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    private float scaleFactor = 1.0f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gallery);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnRotateLeft = findViewById(R.id.btnRotateLeft);
        btnRotateRight = findViewById(R.id.btnRotateRight);

        imageUris = new ArrayList<>();

        openImagePicker();

        // Initialize ScaleGestureDetector for zooming
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Set up button listeners
        btnPrevious.setOnClickListener(v -> showPreviousImage());
        btnNext.setOnClickListener(v -> showNextImage());
        btnRotateLeft.setOnClickListener(v -> rotateImage(-90));
        btnRotateRight.setOnClickListener(v -> rotateImage(90));

        // Enable dragging and zooming
        imageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    // Start dragging
                    break;
                case MotionEvent.ACTION_MOVE:
                    // Move the image
                    matrix.postTranslate(event.getX() - event.getHistorySize(), event.getY() - event.getHistorySize());
                    imageView.setImageMatrix(matrix);
                    break;
            }
            return true;
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple images selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    // Single image selected
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);
                }

                if (!imageUris.isEmpty()) {
                    showImage(currentPosition);
                } else {
                    Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    // Load images from the gallery
//    private void loadImages() {
//        imagePaths = new ArrayList<>();
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
//                imagePaths.add(path);
//            }
//            cursor.close();
//        }
//
//        if (imagePaths.isEmpty()) {
//            Toast.makeText(this, "No images found in gallery", Toast.LENGTH_SHORT).show();
//        } else {
//            showImage(currentPosition);
//        }
//    }

    // Show the image at the specified position
    private void showImage(int position) {
        if (position >= 0 && position < imageUris.size()) {
            Uri imageUri = imageUris.get(position);
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                matrix.reset();
                imageView.setImageMatrix(matrix);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Show the previous image
    private void showPreviousImage() {
        if (currentPosition > 0) {
            currentPosition--;
            showImage(currentPosition);
        }
    }

    // Show the next image
    private void showNextImage() {
        if (currentPosition < imageUris.size() - 1) {
            currentPosition++;
            showImage(currentPosition);
        }
    }

    // Rotate the image by the specified degrees
    private void rotateImage(float degrees) {
        matrix.postRotate(degrees, imageView.getWidth() / 2f, imageView.getHeight() / 2f);
        imageView.setImageMatrix(matrix);
    }

    // Handle zooming
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f)); // Limit the scale
            matrix.setScale(scaleFactor, scaleFactor);
            imageView.setImageMatrix(matrix);
            return true;
        }
    }
}