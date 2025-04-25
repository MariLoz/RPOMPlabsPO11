package by.rpomp.lab_4.tasks;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadFileTask extends Worker {
    private OkHttpClient client = new OkHttpClient();

    public DownloadFileTask(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //String fileUrl = "https://ntv.ifmo.ru/file/journal/1.pdf";
        //String fileUrl = "https://arxiv.org/pdf/2503.04893";
        String fileUrl = getInputData().getString("fileUrl");

        String fileName = "";
        String filePath = "";
        String regex = "\\.([^\\.]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileUrl);
        if (fileUrl.contains(".pdf")) {
            fileName = getFilenameFromUrl(fileUrl);
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName;
        } else {
            if (matcher.find()) {
                fileName = matcher.group(1);
                System.out.println(fileName);
            } else {
                fileName = getFilenameFromUrl(fileUrl);
            }
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileName + ".pdf";
        }

        if (fileUrl == null) {
            Log.e("DownloadFileTask", "URL not provided");
            return Result.failure();
        }

        Request request = new Request.Builder().url(fileUrl).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e("DownloadFileTask", "Failed to download file: " + response);
                return Result.failure();
            }

            if (response.code() == 404) {
                Log.e("DownloadFileTask", "File not found: " + response);
                return Result.failure();
            }

            try (InputStream inputStream = response.body().byteStream(); FileOutputStream outputStream = new FileOutputStream(filePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytes = response.body().contentLength();
                long downloadedBytes = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    downloadedBytes += bytesRead;
                    int progress = (int) ((downloadedBytes * 100) / totalBytes);

                    setProgressAsync(new Data.Builder().putInt("progress", progress).build());
                }
                System.out.println("File downloaded successfully: " + filePath);
            }
        } catch (IOException e) {
            Log.e("DownloadFileTask", "IOException during download", e);
            return Result.failure();
        }

        return Result.success();
    }

    public String getFilenameFromUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            return Paths.get(path).getFileName().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
