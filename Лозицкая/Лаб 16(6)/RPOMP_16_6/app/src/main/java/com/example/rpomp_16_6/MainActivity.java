package com.example.rpomp_16_6;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GestureOverlayView.OnGesturePerformedListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int MEDIA_PICK_REQUEST = 100;
    private String[] neededPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private GestureLibrary gLib;
    private TextView tvContent;
    private String currentMediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermissions()) {
            requestPermissions(neededPermissions, PERMISSION_REQUEST_CODE);
        }

        tvContent = findViewById(R.id.tv_content);
        showMainScreen();

        // Инициализация жестов
        gLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gLib.load()) finish();

        GestureOverlayView gestureView = findViewById(R.id.gestureOverlay);
        gestureView.addOnGesturePerformedListener(this);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gLib.recognize(gesture);
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String name = predictions.get(0).name;
            handleGesture(name);
        }
    }

    private void handleGesture(String gestureName) {
        showToast("Распознан жест: " + gestureName);
        switch (gestureName) {
            case "0":
                showMainScreen();
                break;
            case "1": case "2": case "3": case "4": case "5": case "6": case "7":
                String fileName = gestureName + ".pdf";
                File pdfFile = new File(getPdfFolder(), fileName);
                if (pdfFile.exists()) {
                    openPdfFile(pdfFile);
                } else {
                    downloadFile(fileName);
                }
                break;
            case "up":
                openCamera();
                break;
            case "down":
                openVoiceRecorder();
                break;
            case "media_photo_square":
                openMedia("image");
                break;
            case "media_video_circle":
                openMedia("video");
                break;
            case "media_audio_triangle":
                openMedia("audio");
                break;
        }
    }

    private File getPdfFolder() {
        File folder = new File(getFilesDir(), "pdfs");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    private void downloadFile(String fileName) {
        if (!isNetworkAvailable()) {
            showToast("Нет интернет-соединения");
            return;
        }

        new DownloadFileTask().execute(fileName);
    }

    private class DownloadFileTask extends AsyncTask<String, Void, Boolean> {
        private String fileName;

        @Override
        protected Boolean doInBackground(String... params) {
            fileName = params[0];
            String url = "https://mariloz.github.io/FilesForRPOMP/" + fileName;
            File outputFile = new File(getPdfFolder(), fileName);

            try {
                URL downloadUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return false;
                }

                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                output.close();
                input.close();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                File file = new File(getPdfFolder(), fileName);
                openPdfFile(file);
                showToast("Файл загружен и открывается");
            } else {
                showToast("Ошибка загрузки файла");
            }
        }
    }

    private void openPdfFile(File file) {
        Uri uri = FileProvider.getUriForFile(
                MainActivity.this,
                getPackageName() + ".fileprovider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            showToast("Установите PDF-ридер");
        }
    }

    // Оставшиеся методы без изменений
    private void showMainScreen() {
        tvContent.setText("      Главный экран\nВозможности:\n - Жесты 1-7 для загрузки соответствующих PDF файлов\n" +
                " - Свайп вверх чтобы снять фото\n - Свайп вниз чтобы записать аудио\n" +
                " - Квадрат для просмотра фото\n - Круг для просмотра видео\n - Треугольник для просмотра аудио");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void openCamera() {
        try {
            // Проверка разрешения камеры
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                showToast("Требуется разрешение камеры");
                return;
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = createTempMediaFile("IMG_", ".jpg");

            if (photoFile != null) {
                currentMediaPath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        photoFile
                );

                // Добавляем флаг для временного доступа
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Проверяем наличие активности для обработки интента
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                    showToast("Открываю камеру");
                } else {
                    showToast("Не найдено приложение для камеры");
                }
            }
        } catch (SecurityException e) {
            showToast("Ошибка безопасности: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showToast("Ошибка URI: " + e.getMessage());
        } catch (Exception e) {
            showToast("Ошибка камеры: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    private void openVoiceRecorder() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                showToast("Требуется разрешение микрофона");
                return;
            }

            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 2);
                showToast("Открываю диктофон");
            } else {
                showToast("Не найдено приложение для записи звука");
            }
        } catch (Exception e) {
            showToast("Ошибка: " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MEDIA_PICK_REQUEST && data != null) {
                Uri uri = data.getData();
                openSelectedMedia(uri);
            } else if (requestCode == 1) { // Фото
                File photoFile = new File(currentMediaPath);
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        photoFile
                );
                showMediaPreview(photoUri);
            } else if (requestCode == 2) { // Аудио
                if (data != null && data.getData() != null) {
                    Uri audioUri = data.getData();
                    showMediaPreview(audioUri);
                }
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection;
        Uri contentUri;

        // Определяем тип контента
        if (uri.toString().contains("audio")) {
            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Audio.Media.DATA};
        } else {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[]{MediaStore.Images.Media.DATA};
        }

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri.getPath();
    }

    private boolean checkPermissions() {
        ArrayList<String> requiredPermissions = new ArrayList<>();
        for (String perm : neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(perm);
            }
        }
        if (!requiredPermissions.isEmpty()) {
            requestPermissions(requiredPermissions.toArray(new String[0]),
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(!checkPermissions()) {
                Toast.makeText(this,
                        "Для работы приложения требуются все разрешения!",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showMediaPreview(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getContentResolver().getType(uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
            showToast("Открываю медиафайл");
        } catch (ActivityNotFoundException e) {
            showToast("Нет подходящего приложения");
        }
    }

    private void openMedia(String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        switch(type) {
            case "image":
                intent.setType("image/*");
                showToast("Выберите изображение");
                break;
            case "video":
                intent.setType("video/*");
                showToast("Выберите видео");
                break;
            case "audio":
                intent.setType("audio/*");
                showToast("Выберите аудио");
                break;
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, MEDIA_PICK_REQUEST);
        } catch (ActivityNotFoundException e) {
            showToast("Установите файловый менеджер");
        }
    }

    private File createTempMediaFile(String prefix, String suffix) {
        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                showToast("Не удалось создать папку");
                return null;
            }
            File file = File.createTempFile(prefix, suffix, storageDir);
            currentMediaPath = file.getAbsolutePath(); // Сохраняем актуальный путь
            return file;
        } catch (Exception e) {
            showToast("Ошибка создания файла: " + e.getMessage());
            return null;
        }
    }

    private void openSelectedMedia(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getContentResolver().getType(uri));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
            showToast("Открываю файл");
        } catch (Exception e) {
            showToast("Не удалось открыть файл");
        }
    }
}