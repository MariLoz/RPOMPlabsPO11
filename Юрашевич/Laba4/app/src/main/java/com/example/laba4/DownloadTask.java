package com.example.laba4;

import android.os.AsyncTask;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Void, File> {
    private File downloadDir;
    private MainActivity activity;

    public DownloadTask(MainActivity activity, File downloadDir) {
        this.activity = activity;
        this.downloadDir = downloadDir;
    }

    @Override
    protected File doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream input = connection.getInputStream();
                File outputFile = new File(downloadDir, "downloaded_file.pdf");
                FileOutputStream output = new FileOutputStream(outputFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }

                output.close();
                input.close();
                return outputFile;
            }
        } catch (Exception e) {
            Log.e("DownloadTask", "Ошибка загрузки файла", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            activity.runOnUiThread(() -> {
                activity.showToast("Файл загружен");
                activity.enableButtons(true);
            });
        } else {
            activity.runOnUiThread(() ->
                    activity.showToast("Ошибка загрузки файла"));
        }
    }
}