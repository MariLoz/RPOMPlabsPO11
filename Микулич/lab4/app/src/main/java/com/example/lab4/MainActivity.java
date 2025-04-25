package com.example.lab4;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText editTextIndex;
    private Button btnDownload, btnOpen;
    private ProgressBar progressBar;
    private TextView fileInfoTextView;


    private Uri downloadedUri;

    TextView statusView;

    private final ExecutorService downloadExecutor = Executors.newFixedThreadPool(1);

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


        editTextIndex = findViewById(R.id.editTextIndex);
        btnDownload = findViewById(R.id.btnDownload);
        btnOpen = findViewById(R.id.btnOpen);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        fileInfoTextView = findViewById(R.id.fileInfoTextView);
        fileInfoTextView.setVisibility(View.GONE);
        btnOpen.setVisibility(View.GONE);

        btnDownload.setOnClickListener(v -> downloadPDF());
        btnOpen.setOnClickListener(v -> openFile());

        new Handler(Looper.getMainLooper()).postDelayed(this::showInstructionPopup, 500);
    }


    public void downloadPDF() {
        String fileName = editTextIndex.getText().toString();
        if (fileName.isEmpty()) {
            Toast.makeText(this, "Введите индекс файла!", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileUrl = "https://github.com/Perhewz-Hellcat/android-lab/raw/main/pdf-files/" + fileName + ".pdf";
        new DownloadFileTask(fileName).execute(fileUrl);
    }

    private class DownloadFileTask extends AsyncTask<String, Integer, String> {
        private Uri fileUri;
        private String fileName;
        private int fileSize;

        public DownloadFileTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }

        @Override
        protected String doInBackground(String... urls) {
            String fileUrl = urls[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Ошибка сервера: " + connection.getResponseCode();
                }

                fileSize = connection.getContentLength();


                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, editTextIndex.getText().toString() + ".pdf");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                downloadedUri = fileUri;

                if (fileUri == null) return "Ошибка создания файла";

                InputStream input = new BufferedInputStream(connection.getInputStream());
                OutputStream output = resolver.openOutputStream(fileUri);

                byte[] buffer = new byte[4096];
                int totalBytes = 0;
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytes += bytesRead;
                    output.write(buffer, 0, bytesRead);

                    if (fileSize > 0) {
                        int progress = (int) ((totalBytes * 100L) / fileSize);
                        publishProgress(progress);
                    }
                }

                output.close();
                input.close();

                return "Файл загружен успешно!";
            } catch (Exception e) {
                return "Ошибка загрузки: " + e.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();

            if (result.equals("Файл загружен успешно!")) {
                btnOpen.setVisibility(View.VISIBLE);
                fileInfoTextView.setVisibility(View.VISIBLE);
                fileInfoTextView.setText("Файл: " + fileName + ".pdf\nРазмер: " + (fileSize / 1024) + " KB");
            }
        }
    }



    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(downloadedUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void showInstructionPopup() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean doNotShowAgain = prefs.getBoolean("do_not_show_again", false);

        if (doNotShowAgain) return;

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_instriction, null);
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setElevation(10);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setOutsideTouchable(true);

        View rootView = findViewById(android.R.id.content);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = 0.5f;
        getWindow().setAttributes(layoutParams);

        popupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 1.0f;
            getWindow().setAttributes(lp);
        });

        CheckBox checkBox = popupView.findViewById(R.id.checkBoxDoNotShow);
        Button btnOk = popupView.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("do_not_show_again", true);
                editor.apply();
            }
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }


    public static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        String[] projection = {MediaStore.MediaColumns.DATA};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("getPathFromUri", "Ошибка при получении пути из URI: " + e.getMessage());
        }
        return path;


    }
}

