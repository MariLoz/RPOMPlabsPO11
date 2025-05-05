package com.example.lab4;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownloadActivity extends AppCompatActivity {

    private EditText journalId;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        journalId = findViewById(R.id.journalId);
        downloadButton = findViewById(R.id.downloadButton);

        downloadButton.setOnClickListener(v -> {
            String id = journalId.getText().toString();
            if (!id.isEmpty()) {
                new DownloadFile().execute("https://ntv.ifmo.ru/file/journal/" + id + ".pdf");
            } else {
                Toast.makeText(DownloadActivity.this, "Введите идентификатор журнала", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... f_url) {
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(getFilesDir() + "/journal.pdf");

                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
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
        protected void onPostExecute(String message) {
            Toast.makeText(DownloadActivity.this, message, Toast.LENGTH_LONG).show();
        }
    }
}