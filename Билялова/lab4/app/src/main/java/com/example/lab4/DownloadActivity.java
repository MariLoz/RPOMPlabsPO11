package com.example.lab4;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadActivity extends AppCompatActivity {

    private EditText journalId;
    private Button downloadButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private File downloadedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        journalId = findViewById(R.id.journalId);
        downloadButton = findViewById(R.id.downloadButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        // Скрываем ProgressBar и текст в начале
        progressBar.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);

        downloadButton.setOnClickListener(v -> {
            String id = journalId.getText().toString();
            if (!id.isEmpty()) {
                String fileUrl = "https://ojkum.ru/arc.html" + id + ".pdf";
                new DownloadFile().execute(fileUrl);
            } else {
                Toast.makeText(DownloadActivity.this, "Введите идентификатор журнала", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressText.setText("0%");
        }

        @Override
        protected String doInBackground(String... f_url) {
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                if (!dir.exists()) dir.mkdirs();
                downloadedFile = new File(dir, "journal.pdf");
                OutputStream output = new FileOutputStream(downloadedFile);

                byte[] data = new byte[1024];
                int count;
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        publishProgress(progress);
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                return "Файл успешно скачан";
            } catch (Exception e) {
                return "Ошибка: " + e.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            progressBar.setProgress(progress[0]);
            progressText.setText(progress[0] + "%");
        }

        @Override
        protected void onPostExecute(String message) {
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            Toast.makeText(DownloadActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}
