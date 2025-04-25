package com.example.lab7;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private int currentCameraId = CameraSelector.LENS_FACING_BACK;

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 102;

    private int getImageOrientation(String imagePath) {
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }
    private Bitmap rotateBitmapBasedOnExif(Bitmap bitmap, String imagePath) {
        int orientation = getImageOrientation(imagePath);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.postScale(1, -1);
                break;
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        Button btnBack = findViewById(R.id.backCameraButton);

        if (checkCameraPermission()) {
            startCamera();
        } else {
            requestCameraPermission();
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onClickTakePhotoButton(View v){
        takePhoto();
    }

    public void onClickSwitchCameraButton(View v){
        switchCamera();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Разрешение на использование камеры отклонено", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение на запись предоставлено", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Разрешение на запись отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder().build();
                cameraSelector = new CameraSelector.Builder().requireLensFacing(currentCameraId).build();
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка при запуске камеры", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void switchCamera() {
        if (currentCameraId == CameraSelector.LENS_FACING_BACK) {
            currentCameraId = CameraSelector.LENS_FACING_FRONT;
        } else {
            currentCameraId = CameraSelector.LENS_FACING_BACK;
        }
        startCamera();
    }

    @NonNull
    private Uri saveImageInAndroidApi29AndAbove(@NonNull final Bitmap bitmap) throws IOException {
        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + System.currentTimeMillis());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
        }
        final ContentResolver resolver = getContentResolver();
        Uri uri = null;
        try {
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            uri = resolver.insert(contentUri, values);
            if (uri == null) {
                throw new IOException("Ошибка создания новой MediaStore записи.");
            }
            try (final OutputStream stream = resolver.openOutputStream(uri)) {
                if (stream == null) {
                    throw new IOException("Ошибка открытия потока вывода.");
                }
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream)) {
                    throw new IOException("Ошибка сохранения bitmap.");
                }
            }
            return uri;
        } catch (IOException e) {
            if (uri != null) {
                resolver.delete(uri, null, null);
            }
            throw e;
        }
    }

    private void takePhoto() {
        if (imageCapture == null) {
            Toast.makeText(this, "Камера не инициализирована", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Разрешение на запись не предоставлено", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            return;
        }

        File photoFile = new File(getExternalFilesDir(null), createFileName());

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        new Thread(() -> {
                            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            if (bitmap != null) {
                                Bitmap rotatedBitmap = rotateBitmapBasedOnExif(bitmap, photoFile.getAbsolutePath());

                                int newWidth = 1024;
                                int newHeight = (int) (rotatedBitmap.getHeight() * ((float) newWidth / rotatedBitmap.getWidth()));
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true);

                                try {
                                    Uri imageUri = saveImageInAndroidApi29AndAbove(scaledBitmap);

                                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    mediaScanIntent.setData(imageUri);
                                    sendBroadcast(mediaScanIntent);

                                    runOnUiThread(() -> {
                                        Intent intent = new Intent(CameraActivity.this, ImageActivity.class);
                                        intent.putExtra("imageUri", imageUri.toString());
                                        startActivity(intent);
                                    });

                                    photoFile.delete();
                                    rotatedBitmap.recycle();
                                    scaledBitmap.recycle();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Ошибка", Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                runOnUiThread(() -> Toast.makeText(CameraActivity.this, "Ошибка создания изображения", Toast.LENGTH_SHORT).show());
                            }
                        }).start();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        exception.printStackTrace();
                        Toast.makeText(CameraActivity.this, "Ошибка при захвате снимка: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NonNull
    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        return "PHOTO_" + timeStamp + ".jpg";
    }
}