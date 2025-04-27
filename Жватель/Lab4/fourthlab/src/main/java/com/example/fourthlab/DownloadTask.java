package com.example.fourthlab;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


import androidx.annotation.RequiresApi;

public class DownloadTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "DownloadTask";
    private static final String SERVER_ADDRESS = "51.250.54.78";
    private static final int SERVER_PORT = 443;
    private Context context;

    public DownloadTask(Context context) {
        this.context = context;
    }

    private static final String HOST = "ntv.ifmo.ru";
    private static final int PORT = 443;
    private static final String PATH = "/file/journal/";
    private static final String MAIN_PAGE_URL = "https://ntv.ifmo.ru/";

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected String doInBackground(String... params) {
        String journalId = params[0];
        SSLSocket socket = null;
        DataOutputStream outputStreamToServer = null;
        DataInputStream inputStreamFromServer = null;
        OutputStream fileOutputStream = null;

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, "journal.pdf");
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            values.put(MediaStore.Downloads.IS_PENDING, 1);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri == null) {
                return "Failed to create file in Downloads";
            }

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(HOST, PORT);
            socket.setSoTimeout(30000);
            String[] enabledProtocols = {"TLSv1.2", "TLSv1.3"};
            socket.setEnabledProtocols(enabledProtocols);

            outputStreamToServer = new DataOutputStream(socket.getOutputStream());
            inputStreamFromServer = new DataInputStream(socket.getInputStream());

            String httpRequest = "GET " + PATH + journalId + ".pdf HTTP/1.1\r\n" +
                    "Host: " + HOST + "\r\n" +
                    "User-Agent: AndroidApp/1.0\r\n" +
                    "Accept: application/pdf\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            Log.d(TAG, "Sending HTTP request: " + httpRequest);
            outputStreamToServer.writeBytes(httpRequest);
            outputStreamToServer.flush();

            StringBuilder responseHeader = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            boolean isHeader = true;
            int contentLength = -1;

            while ((bytesRead = inputStreamFromServer.read(buffer)) != -1) {
                String chunk = new String(buffer, 0, bytesRead);
                responseHeader.append(chunk);
                Log.d(TAG, "Server response chunk: " + chunk);

                if (isHeader) {
                    String headerStr = responseHeader.toString();
                    Log.d(TAG, "Full header: " + headerStr);

                    // Проверка на код ошибки 4xx (например, 404)
                    if (headerStr.contains("HTTP/1.1 4")) {
                        // Очищаем созданный файл
                        resolver.delete(uri, null, null);
                        return "redirect_to_main";
                    }

                    if (headerStr.contains("\r\n\r\n")) {
                        String[] headerLines = headerStr.split("\r\n");
                        for (String line : headerLines) {
                            if (line.startsWith("Content-Length:")) {
                                contentLength = Integer.parseInt(line.replace("Content-Length:", "").trim());
                            }
                        }
                        isHeader = false;
                        int headerEnd = headerStr.indexOf("\r\n\r\n") + 4;
                        byte[] bodyBuffer = new byte[bytesRead - headerEnd];
                        System.arraycopy(buffer, headerEnd, bodyBuffer, 0, bytesRead - headerEnd);
                        fileOutputStream = resolver.openOutputStream(uri);
                        if (fileOutputStream != null) {
                            fileOutputStream.write(bodyBuffer, 0, bytesRead - headerEnd);
                        }
                    }
                } else {
                    if (fileOutputStream != null) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            if (contentLength > 0 && fileOutputStream != null) {
                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bundle bundle = new Bundle();
                    resolver.update(uri, values, bundle);
                } else {
                    resolver.update(uri, values, null, null);
                }

                return "Download completed " + uri.toString();
            } else {
                return "No content or error in response";
            }

        } catch (UnknownHostException e) {
            Log.e(TAG, "Unknown host: " + HOST, e);
            return "Failed to connect to server: Unknown host";
        } catch (IOException e) {
            Log.e(TAG, "Error downloading file via socket", e);
            return "Failed to download: " + e.getMessage();
        } finally {
            try {
                if (outputStreamToServer != null) outputStreamToServer.close();
                if (inputStreamFromServer != null) inputStreamFromServer.close();
                if (socket != null) socket.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams or socket", e);
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "Download result: " + result);
        if (result.equals("redirect_to_main")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MAIN_PAGE_URL));
            context.startActivity(intent);
        }
    }
}