package com.example.kozinlab7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageView capturedImage;
    private Button captureButton, switchCameraButton, viewButton;
    private boolean isFrontCamera = false;
    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView); // Заменили TextureView на PreviewView
        capturedImage = findViewById(R.id.capturedImage);
        captureButton = findViewById(R.id.captureButton);
        switchCameraButton = findViewById(R.id.switchCameraButton);
        viewButton = findViewById(R.id.viewButton);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (allPermissionsGranted()) {
            startCamera(isFrontCamera);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        captureButton.setOnClickListener(v -> takePhoto());

        switchCameraButton.setOnClickListener(v -> {
            isFrontCamera = !isFrontCamera;
            startCamera(isFrontCamera);
        });

        viewButton.setOnClickListener(v -> {
            if (photoFile != null) {
                capturedImage.setImageURI(Uri.fromFile(photoFile));
                capturedImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startCamera(boolean front) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();

                cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(front ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK)
                        .build();

                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider()); // Исправлено

                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                Log.e("CameraX", "Ошибка запуска камеры", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        photoFile = new File(getExternalFilesDir(null), "photo.jpg");

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        runOnUiThread(() -> viewButton.setVisibility(View.VISIBLE));
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("CameraX", "Ошибка сохранения фото", exception);
                    }
                });
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
