package com.example.lab5;

import android.annotation.SuppressLint;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ImageActivity extends AppCompatActivity{
    private ImageView imageView;
    private Uri imageUri;
    private float scale;
    private float dX, dY;
    private final Matrix matrix = new Matrix();

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainImage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        ImageButton zoomInButton = findViewById(R.id.zoomInButton);
        ImageButton zoomOutButton = findViewById(R.id.zoomOutButton);

        String uriString = getIntent().getStringExtra("imageUri");
        if (uriString != null) {
            imageUri = Uri.parse(uriString);
            imageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
        }

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale += 0.1f;
                matrix.setScale(scale, scale);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageMatrix(matrix);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale -= 0.1f;
                if (scale < 0.1f) scale = 0.1f;
                matrix.setScale(scale, scale);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageMatrix(matrix);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = event.getRawX();
                        dY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - dX;
                        float deltaY = event.getRawY() - dY;

                        matrix.postTranslate(deltaX, deltaY);
                        imageView.setImageMatrix(matrix);

                        dX = event.getRawX();
                        dY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;

                    default:
                        return false;
                }
            }
        });

        if (imageUri != null) {
            imageView.setImageURI(imageUri);
        }

        scale = imageView.getScaleY();
    }
}