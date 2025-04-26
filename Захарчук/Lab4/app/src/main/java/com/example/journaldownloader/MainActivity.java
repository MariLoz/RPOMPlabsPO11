package com.example.journaldownloader;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);
    private static final int STORAGE_PERMISSION_CODE = 100;
    private EditText journalIdEditText;
    private Button downloadButton, viewButton, deleteButton, taskInfoButton, authorButton;
    private TextView statusTextView;
    private File downloadedFile;
    private static final String PREFS_NAME = "JournalPrefs";
    private static final String PREF_DONT_SHOW = "dontShowInstructions";
    private static final String DOWNLOAD_DIR = "JournalDownloads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOG.info("Starting MainActivity");
        setContentView(R.layout.activity_main);

        journalIdEditText = findViewById(R.id.journalIdEditText);
        downloadButton = findViewById(R.id.downloadButton);
        viewButton = findViewById(R.id.viewButton);
        deleteButton = findViewById(R.id.deleteButton);
        statusTextView = findViewById(R.id.statusTextView);
        taskInfoButton = findViewById(R.id.taskInfoButton);
        authorButton = findViewById(R.id.authorButton);

        View rootView = findViewById(android.R.id.content);
        rootView.post(() -> showInstructionsIfNeeded());

        if (checkPermission()) {
            LOG.info("Storage permission granted, setting up app");
            setupDownloadDirectory();
            setupListeners();
        } else {
            LOG.warn("Storage permission not granted, requesting");
            requestPermission();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        LOG.debug("Checking storage permission: {}", result == PackageManager.PERMISSION_GRANTED);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        LOG.info("Requesting storage permission");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LOG.info("Storage permission granted by user");
                setupDownloadDirectory();
                setupListeners();
            } else {
                LOG.warn("Storage permission denied by user");
                Toast.makeText(this, "Разрешение на запись необходимо для работы приложения",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setupDownloadDirectory() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                DOWNLOAD_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.error("Failed to create download directory at: {}", dir.getAbsolutePath());
            Toast.makeText(this, "Не удалось создать директорию", Toast.LENGTH_SHORT).show();
        } else {
            LOG.debug("Download directory ready at: {}", dir.getAbsolutePath());
        }
    }

    private void setupListeners() {
        downloadButton.setOnClickListener(v -> {
            String journalId = journalIdEditText.getText().toString();
            if (!journalId.isEmpty()) {
                LOG.info("Starting download for journal ID: {}", journalId);
                new SocketDownloadTask().execute(journalId);
            } else {
                LOG.warn("Empty journal ID entered");
                Toast.makeText(this, "Введите ID журнала", Toast.LENGTH_SHORT).show();
            }
        });

        viewButton.setOnClickListener(v -> viewFile());
        deleteButton.setOnClickListener(v -> deleteFile());
        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
    }

    private void showInstructionsIfNeeded() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean dontShow = prefs.getBoolean(PREF_DONT_SHOW, false);
        LOG.debug("Checking show instructions preference: dontShow={}", dontShow);

        if (dontShow) {
            LOG.debug("Instructions popup skipped due to preference");
            return;
        }

        LOG.info("Attempting to show instructions popup");
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            LOG.error("LayoutInflater is null");
            return;
        }

        View popupView = inflater.inflate(R.layout.popup_instructions, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        CheckBox dontShowCheckBox = popupView.findViewById(R.id.dontShowCheckBox);
        Button okButton = popupView.findViewById(R.id.okButton);

        okButton.setOnClickListener(v -> {
            LOG.debug("OK button clicked, checkbox state: {}", dontShowCheckBox.isChecked());
            if (dontShowCheckBox.isChecked()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(PREF_DONT_SHOW, true);
                editor.apply();
                LOG.info("Instructions set to not show again");
            }
            popupWindow.dismiss();
            LOG.info("Popup window dismissed");
        });

        try {
            popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            LOG.info("Popup window shown successfully");
        } catch (Exception e) {
            LOG.error("Failed to show popup window", e);
            Toast.makeText(this, "Ошибка при показе инструкции", Toast.LENGTH_SHORT).show();
        }
    }

    private class SocketDownloadTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            statusTextView.setText("Проверка соединения (Socket HTTPS)...");
            downloadButton.setEnabled(false);
            LOG.debug("Starting socket download task");
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String journalId = params[0];
            String host = "ntv.ifmo.ru";
            String path = "/file/journal/" + journalId + ".pdf";
            int port = 443;

            SSLSocket socket = null;
            PrintWriter out = null;
            InputStream in = null;
            FileOutputStream fileOutput = null;

            try {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo == null || !networkInfo.isConnected()) {
                    errorMessage = "Нет интернет-соединения";
                    LOG.warn("No internet connection");
                    return false;
                }

                LOG.debug("Connecting to {}:{}", host, port);
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = (SSLSocket) factory.createSocket(host, port);
                socket.setSoTimeout(10000);

                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("GET " + path + " HTTP/1.1");
                out.println("Host: " + host);
                out.println("Connection: close");
                out.println();

                in = socket.getInputStream();
                ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    responseBuffer.write(buffer, 0, bytesRead);
                }
                byte[] fullResponse = responseBuffer.toByteArray();

                String responseString = new String(fullResponse, "UTF-8");
                int headerEndIndex = responseString.indexOf("\r\n\r\n");
                if (headerEndIndex == -1) {
                    errorMessage = "Некорректный ответ сервера: заголовки не завершены";
                    LOG.error("Invalid server response: headers not found");
                    return false;
                }

                String headers = responseString.substring(0, headerEndIndex);
                LOG.debug("Headers: {}", headers);

                String firstLine = headers.split("\n")[0];
                if (!firstLine.contains("200 OK")) {
                    errorMessage = "Сервер вернул ошибку: " + firstLine;
                    LOG.warn("Socket response: {}", firstLine);
                    return false;
                }

                byte[] body = new byte[fullResponse.length - headerEndIndex - 4];
                System.arraycopy(fullResponse, headerEndIndex + 4, body, 0, body.length);

                if (body.length > 10) {
                    StringBuilder hex = new StringBuilder();
                    for (int i = 0; i < 10; i++) {
                        hex.append(String.format("%02X ", body[i]));
                    }
                    LOG.debug("First 10 bytes of body: {}", hex.toString());
                }

                File dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), DOWNLOAD_DIR);
                downloadedFile = new File(dir, "journal_" + journalId + ".pdf");
                LOG.info("Saving file to: {}", downloadedFile.getAbsolutePath());

                fileOutput = new FileOutputStream(downloadedFile);
                fileOutput.write(body);
                LOG.info("Socket download completed, bytes downloaded: {}", body.length);

                return true;

            } catch (IOException e) {
                errorMessage = "Ошибка загрузки через сокет: " + e.getMessage();
                LOG.error("Socket download failed", e);
                return false;
            } finally {
                try {
                    if (fileOutput != null) fileOutput.close();
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    LOG.error("Error closing socket resources", e);
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            downloadButton.setEnabled(true);
            if (success) {
                statusTextView.setText("Файл успешно загружен через сокет");
                viewButton.setEnabled(true);
                deleteButton.setEnabled(true);
                LOG.info("Socket download task completed successfully");
            } else {
                statusTextView.setText(errorMessage);
                viewButton.setEnabled(false);
                deleteButton.setEnabled(false);
                LOG.warn("Socket download task failed: {}", errorMessage);
            }
        }
    }

    private void viewFile() {
        if (downloadedFile != null && downloadedFile.exists()) {
            LOG.info("Attempting to view file: {}", downloadedFile.getAbsolutePath());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = FileProvider.getUriForFile(this,
                    "com.example.journaldownloader.fileprovider", downloadedFile);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager pm = getPackageManager();
            List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
            if (activities.size() > 0) {
                try {
                    startActivity(intent);
                    LOG.debug("File viewer launched successfully");
                } catch (Exception e) {
                    LOG.error("Failed to open PDF viewer", e);
                    Toast.makeText(this, "Ошибка при открытии PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                LOG.warn("No PDF viewer application found");
                Toast.makeText(this, "Не установлено приложение для просмотра PDF", Toast.LENGTH_LONG).show();
            }
        } else {
            LOG.warn("File not found for viewing: {}", downloadedFile != null ? downloadedFile.getAbsolutePath() : "null");
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFile() {
        if (downloadedFile != null && downloadedFile.exists()) {
            LOG.info("Attempting to delete file: {}", downloadedFile.getAbsolutePath());
            if (downloadedFile.delete()) {
                statusTextView.setText("Файл удален");
                viewButton.setEnabled(false);
                deleteButton.setEnabled(false);
                LOG.info("File deleted successfully");
            } else {
                LOG.error("Failed to delete file");
                Toast.makeText(this, "Не удалось удалить файл", Toast.LENGTH_SHORT).show();
            }
        } else {
            LOG.warn("File not found for deletion: {}", downloadedFile != null ? downloadedFile.getAbsolutePath() : "null");
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
    }
}