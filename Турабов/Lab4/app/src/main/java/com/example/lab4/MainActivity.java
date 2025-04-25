package com.example.lab4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ArrayList<PDFLog> logs = new ArrayList<PDFLog>();

    ListView logsList;
    LogsAdapter pdfLogArrayAdapter;
    private File downloaded;

    private Uri selected, downloadedUri;
    private final ExecutorService downloadExecutor = Executors.newFixedThreadPool(1);

    private Button download, show, delete;
    EditText filename;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String SHOW_POPUP_KEY = "showPopup";
    private static final String AMOUNT_KEY = "amount";

    private static final String TIME_KEY = "time";

    private static final String TITLE_KEY = "title";

    private static final String URI_KEY = "uri";

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

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadHistory();

        filename = findViewById(R.id.filename);
        download = findViewById(R.id.download_button);
        show = findViewById(R.id.show_button);
        delete = findViewById(R.id.delete_file);

        show.setEnabled(false);
        delete.setEnabled(false);

        logsList = findViewById(R.id.logs);
        pdfLogArrayAdapter = new LogsAdapter(this, R.layout.logs_layout, logs);
        logsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = logs.get(position).getUri();

                downloadedUri = logs.get(position).getUri();


                view.setSelected(true);
                show.setEnabled(true);
                delete.setEnabled(true);
            }
        });
        logsList.setAdapter(pdfLogArrayAdapter);

        boolean showPopup = prefs.getBoolean(SHOW_POPUP_KEY, true);
        if (showPopup) {
            findViewById(android.R.id.content).post(this::showPopupInstruction);
        }
    }

        public void downloadFile (View view){

            //unselect items
            if (logsList.getSelectedView() != null)
                logsList.getSelectedView().setSelected(false);

            String name = filename.getText().toString();
            download.setText("Downloading...");


            String url = "https://ntv.ifmo.ru/file/journal/" + name + ".pdf";

            downloadPDF(url);
        }

        public void downloadPDF(String urlPdf)
        {
            downloadExecutor.execute(() -> {
                String fileUrl = urlPdf;
                File pdfFile = null;
                Uri uri = null;
                try {
                    URL url = new URL(fileUrl);
                    Log.println(Log.ERROR, "me", "1111");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    //connection.setDoOutput(true);
                    //connection.connect();

                    Log.println(Log.ERROR, "me", "1111");
                    ContentResolver resolver = getContentResolver();
                    Log.println(Log.ERROR, "me", "1111");

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename.getText().toString() + ".pdf");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                    uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    downloadedUri = uri;

                    Log.println(Log.ERROR, "me", "7777");

                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.println(Log.ERROR, "me", "333333");
                        download.setText("Download(Error)");
                    } else {

                        Log.println(Log.ERROR, "me", "000");
                        //File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        pdfFile = new File(getPathFromUri(this, uri));
                        Log.println(Log.ERROR, "me", "000");

                        InputStream inputStream = connection.getInputStream();
                        Log.println(Log.ERROR, "me", "000");
                        OutputStream outputStream = resolver.openOutputStream(uri);
                        Log.println(Log.ERROR, "me", "000");


                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        Log.println(Log.ERROR, "me", "000");
                        outputStream.close();
                        inputStream.close();
                        Log.println(Log.ERROR, "me", "222");


                        Log.println(Log.ERROR, "me", "3333");
                        logs.add(new PDFLog(LocalDateTime.now().toString(), filename.getText().toString(), uri));

                    }

                } catch (Exception e) {
                }
                downloadedUri = uri;
                downloaded = pdfFile;
                runOnUiThread(() -> {
                    pdfLogArrayAdapter.notifyDataSetChanged();
                    saveHistory();
                    download.setText("Download");
                    showPDF(downloadedUri);
                    show.setEnabled(true);
                    delete.setEnabled(true);
                });

            });
        }

        public static String getPathFromUri (Context context, Uri uri){
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

        public void showFile (View v)
        {
            showPDF(downloadedUri);
        }

        public void showPDF (Uri uri)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }

        public void deleteFile (View view){

            for (int i = 0; i < logs.size(); i++)
            {
                if(logs.get(i).getUri() == downloadedUri)
                {
                    logs.remove(i);
                    break;
                }
            }
            deletePDF(downloadedUri);
            pdfLogArrayAdapter.notifyDataSetChanged();
            saveHistory();
            show.setEnabled(false);
            delete.setEnabled(false);
        }

        public void deletePDF (Uri uri)
        {
            File pdf = new File(getPathFromUri(this, uri));
            if (pdf.exists()) {
                if (pdf.delete()) {
                    Toast.makeText(this, "File was successfully deleted", Toast.LENGTH_LONG).show();
                    delete.setEnabled(false);
                } else Toast.makeText(this, "Error occurred", Toast.LENGTH_LONG).show();
            }
        }

    private void showPopupInstruction() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_instruction, null);

        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        TextView messageView = popupView.findViewById(R.id.instructionTextView);
        messageView.setText("Enter filename and press download button");

        CheckBox dontShowAgainCheckBox = popupView.findViewById(R.id.dontShowAgainCheckBox);
        Button okButton = popupView.findViewById(R.id.okButton);

        okButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(SHOW_POPUP_KEY, !dontShowAgainCheckBox.isChecked());
            editor.apply();
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);
    }


    private void saveHistory() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(AMOUNT_KEY, logs.size());
        for(int i = 0; i < logs.size(); i++)
        {
            editor.putString(TIME_KEY+String.valueOf(i), logs.get(i).getDate());
            editor.putString(TITLE_KEY+String.valueOf(i), logs.get(i).getName());
            editor.putString(URI_KEY+String.valueOf(i), logs.get(i).getUri().toString());
        }
        editor.apply();
    }

    private void loadHistory() {
        int size = prefs.getInt(AMOUNT_KEY, 0);

        for (int i = 0; i< size; i++)
        {
            logs.add(new PDFLog(prefs.getString(TIME_KEY+String.valueOf(i), LocalDateTime.now().toString()), prefs.getString(TITLE_KEY+String.valueOf(i), "error"), Uri.parse(prefs.getString(URI_KEY+String.valueOf(i), "error"))));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadExecutor.shutdown();
    }

}
