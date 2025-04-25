package com.example.lab4;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText journalId;
    private Button downloadButton, viewButton;
    private ProgressBar progressBar;
    private TextView progressText;
    private File downloadedFile;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        journalId = findViewById(R.id.journalId);
        downloadButton = findViewById(R.id.downloadButton);
        viewButton = findViewById(R.id.viewButton);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        findViewById(R.id.mainLayout).post(this::showFullScreenPopup);

        downloadButton.setOnClickListener(v -> {
            String id = journalId.getText().toString();
            if (!id.isEmpty()) {
                String url = generateDownloadUrl(id);
                if (url != null) {
                    new DownloadFile().execute(url);
                } else {
                    Toast.makeText(MainActivity.this, "Неверный номер выпуска", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Введите номер выпуска", Toast.LENGTH_SHORT).show();
            }
        });

        viewButton.setOnClickListener(v -> {
            if (downloadedFile != null && downloadedFile.exists()) {
                openPdfFile();
            } else {
                Toast.makeText(MainActivity.this, "Файл не скачан", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFullScreenPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        Button okButton = popupView.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.CENTER, 0, 0);
    }

    private void openPdfFile() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", downloadedFile);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressText.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            progressText.setText("0%");
        }

        @Override
        protected String doInBackground(String... f_url) {
            try {
                URL url = new URL(f_url[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                downloadedFile = new File(dir, "journal.pdf");
                OutputStream output = new FileOutputStream(downloadedFile);

                byte[] data = new byte[1024];
                int total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
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
            progressBar.setProgress(progress[0]);
            progressText.setText(progress[0] + "%");
        }

        @Override
        protected void onPostExecute(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            progressText.setVisibility(View.GONE);
            if (downloadedFile != null && downloadedFile.exists()) {
                viewButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private String generateDownloadUrl(String id) {
        try {
            int issueNumber = Integer.parseInt(id);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            int issuePart = getIssuePart(issueNumber);

            if (issuePart == -1) {
                return null;
            }

            String formattedId = String.format("%04d", issueNumber);
            return "https://ojkum.ru/images/full_texts/" + 2024 + "_" + issuePart + "_" + formattedId + ".pdf";

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int getIssuePart(int issueNumber) {
        if (issueNumber >= 68 && issueNumber <= 71) return 1;
        if (issueNumber >= 72 && issueNumber <= 75) return 2;
        if (issueNumber >= 76 && issueNumber <= 79) return 3;
        if (issueNumber >= 80 && issueNumber <= 83) return 4;
        return -1;
    }
}
