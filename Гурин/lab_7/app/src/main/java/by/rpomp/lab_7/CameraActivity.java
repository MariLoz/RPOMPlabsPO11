package by.rpomp.lab_7;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

import by.rpomp.lab_7.utils.YUVtoRGB;

public class CameraActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 83854;

    private ImageView preview;
    private Button saveButton;

    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    YUVtoRGB translator = new YUVtoRGB();
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.camera), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preview = findViewById(R.id.preview);
        saveButton = findViewById(R.id.savePhotoButton);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
        } else {
            initializeCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeCamera();
        }
    }

    private void initializeCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(new Size(700, 1024))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();

                    imageCapture = new ImageCapture.Builder().build();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(CameraActivity.this),
                            new ImageAnalysis.Analyzer() {
                                @Override
                                public void analyze(ImageProxy image) {
                                    Image img = image.getImage();
                                    Bitmap bitmap = translator.translateYUV(img, CameraActivity.this);
                                    preview.setRotation(image.getImageInfo().getRotationDegrees());
                                    preview.setImageBitmap(bitmap);
                                    image.close();
                                }
                            });

                    cameraProvider.bindToLifecycle(CameraActivity.this, cameraSelector, imageAnalysis, imageCapture);

                    // Добавление обработчика кнопки сохранения
                    saveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(
                                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "image.jpg"))
                                    .build();

                            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(CameraActivity.this),
                                    new ImageCapture.OnImageSavedCallback() {
                                        @Override
                                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                                            Toast.makeText(CameraActivity.this, "Фотография сохранена в папке Downloads", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(@NonNull ImageCaptureException exception) {
                                            Toast.makeText(CameraActivity.this, "Ошибка при сохранении фотографии", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(CameraActivity.this));
    }
}
