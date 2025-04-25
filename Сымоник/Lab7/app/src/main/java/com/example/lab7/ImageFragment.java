package com.example.lab7;


import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment {

    private ImageView imageView;
    private Uri imageUri;
    private float scale;
    private float dX, dY;
    private Matrix matrix = new Matrix();


    public ImageFragment(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Инфлейтим layout
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);

        imageView = rootView.findViewById(R.id.imageView);
        Button zoomInButton = rootView.findViewById(R.id.zoomInButton);
        Button zoomOutButton = rootView.findViewById(R.id.zoomOutButton);

        imageView.setImageMatrix(matrix);

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale += 0.1f;  //
                matrix.setScale(scale, scale);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageMatrix(matrix);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scale -= 0.1f;
                if (scale < 0.1f) scale = 0.1f;  //
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

        return rootView;
    }


}
