package com.example.lab4;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String SHOW_POPUP_KEY = "showPopup";
    private static final String HISTORY_KEY = "download_history";

//    private int journalId;
    private EditText journalIdEditText;
    private Button downloadButton;
    private Button viewButton;
    private Button deleteButton;
    private File downloadedFile;
    private Uri downloadedFileUri;
    private final ExecutorService downloadExecutor = Executors.newFixedThreadPool(1);
    private ListView historyListView;
    private List<String> historyList = new ArrayList<>();
    private ArrayAdapter<String> historyAdapter;
    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> openFileLauncher;
    private ProgressBar progressBar;
    private TextView progressText;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
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

        journalIdEditText = findViewById(R.id.journalIdEditText);
        downloadButton = findViewById(R.id.downloadButton);

        historyListView = findViewById(R.id.historyListView);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.status);

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadHistory();
        historyAdapter = new HistoryAdapter(this, historyList);
        historyListView.setAdapter(historyAdapter);
        boolean showPopup = prefs.getBoolean(SHOW_POPUP_KEY, true);
        if (showPopup) {
            findViewById(android.R.id.content).post(this::showPopupInstruction);
        }

        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            String entry = historyList.get(position);
            Uri uri = findFileUriByName(entry);
            openPDFFile(uri);
        });


        openFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Toast.makeText(MainActivity.this, "Нажал Сымоник И.А.", LENGTH_SHORT).show();

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                try {
                                    openPDFFile(uri);
                                } catch (SecurityException e) {
                                    Toast.makeText(this, "Нет прав доступа к файлу", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "URI файла отсутствует", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Данные отсутствуют", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Выбор файла отменен", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    public Uri findFileUriByName(String fileName) {
        ContentResolver resolver = getContentResolver();
        Uri collection;

        // Указываем папку загрузок
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }

        // Указываем столбцы для запроса
        String[] projection = {MediaStore.Downloads._ID};
        String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{fileName};

        // Выполняем запрос
        try (Cursor cursor = resolver.query(collection, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                return ContentUris.withAppendedId(collection, id);
            }
        } catch (Exception e) {
            Log.e("findFileUriByName", "Ошибка при поиске файла: " + e.getMessage());
        }

        return null; // Файл не найден
    }

    private String extractFilePathFromEntry(String entry) {
        String[] parts = entry.split(" - ");
        if (parts.length > 1) {
            String fileName = parts[1].replace("Файл ", "").replace(" загружен", "");

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            return new File(downloadsDir, fileName).getAbsolutePath();
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    public void downloadFile(View v) {
        String journalId = journalIdEditText.getText().toString();


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_download_progress, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        ProgressBar popupProgressBar = popupView.findViewById(R.id.popupProgressBar);
        TextView popupProgressText = popupView.findViewById(R.id.popupProgressText);

        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);

        downloadExecutor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://ntv.ifmo.ru/file/journal/" + journalId + ".pdf")
                    .build();
            Response response = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            Uri uri = null;

            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful() && response.header("Content-Type") != null && Objects.requireNonNull(response.header("Content-Type")).startsWith("application/pdf")) {
                    ContentResolver resolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, journalId + ".pdf");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    }

                    downloadedFileUri = uri;

                    if (uri != null) {
                        outputStream = resolver.openOutputStream(uri);
                        assert response.body() != null;
                        inputStream = response.body().byteStream();

                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        long totalBytesRead = 0;
                        long fileSize = response.body().contentLength();

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                            totalBytesRead += bytesRead;
                            final long finalTotalBytesRead = totalBytesRead;
                            final long finalFileSize = fileSize;

                            runOnUiThread(() -> {
                                int progress = (int) ((finalTotalBytesRead * 100) / finalFileSize);
                                popupProgressBar.setProgress(progress);
                                popupProgressText.setText(progress + "%");
                            });
                        }

                        downloadedFile = new File(getPathFromUri(this, uri));
                        final Uri curi = uri;
                        runOnUiThread(() -> {

                            addHistoryEntry(journalId + ".pdf");
                            openPDFFile(curi);
                            journalIdEditText.setText("");
                            progressText.setText("");
                            popupWindow.dismiss();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        progressText.setText("");
                        popupWindow.dismiss();
                    });
                }

            } catch (IOException e) {
                final String errorMessage = "Ошибка загрузки файла: " + e.getMessage();
                runOnUiThread(() -> {
                    progressText.setText(errorMessage);
                    popupWindow.dismiss();
                });
                Log.e("Download Error", errorMessage, e);
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                    if (response != null) response.close();
                } catch (IOException e) {
                    Log.e("Download Error", "Ошибка закрытия потоков", e);
                }
            }
        });
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

    public void openDownloadedFile(View v) {
        openPDFFile(downloadedFileUri);
    }

    private void openPDFFile(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "Файл не найден.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Не найдено приложение для просмотра PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openPDF(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        openFileLauncher.launch(intent);
    }

    public void deleteFile(View v){
        deletePDF();
    }

    @SuppressLint("SetTextI18n")
    private void deletePDF() {
        if (downloadedFileUri != null) {
            try {
                String filePath = getPathFromUri(this, downloadedFileUri);
                if (filePath != null) {
                    File fileToDelete = new File(filePath);

                    if (fileToDelete.exists() && fileToDelete.delete()) {
                        Log.d("MainActivity", "Файл успешно удален с использованием File API.");

                        updateMediaStoreAfterDeletion(downloadedFileUri);
                        runOnUiThread(() -> {
                            String journalId = journalIdEditText.getText().toString();
                            viewButton.setEnabled(false);
                            deleteButton.setEnabled(false);
                            downloadedFile = null;
                            downloadedFileUri = null;
                            journalIdEditText.setText("");
                            addHistoryEntry("Скачанный файл удалён");
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Не удалось удалить файл.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e("MainActivity", "Не удалось получить путь к файлу из URI.");
                    runOnUiThread(() -> Toast.makeText(this, "Не удалось получить путь к файлу из URI.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Ошибка при удалении файла: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Ошибка при удалении файла: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        } else {
            Log.e("MainActivity", "deleteFile: downloadedFileUri is null");
            runOnUiThread(() -> Toast.makeText(this, "Файл не найден.", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateMediaStoreAfterDeletion(Uri fileUri) {
        ContentResolver resolver = getContentResolver();
        try {
            int rowsDeleted = resolver.delete(fileUri, null, null);
            Log.d("MainActivity", "MediaStore update: Удалено строк: " + rowsDeleted);
            if (rowsDeleted == 0) {
                Log.w("MainActivity", "MediaStore update: Не удалено ни одной строки. Возможно, файл не зарегистрирован должным образом.");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getContentResolver().notifyChange(MediaStore.Downloads.EXTERNAL_CONTENT_URI, null);
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "MediaStore update: Ошибка уведомления об удалении", e);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showPopupInstruction() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_instruction, null);

        int width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        int height = ConstraintLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        TextView messageView = popupView.findViewById(R.id.instructionTextView);
        messageView.setText("Разработал Сымоник И. РО-11.");

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

    @SuppressLint("SimpleDateFormat")
    private void addHistoryEntry(String message) {
        historyList.add(0, message);
        historyAdapter.notifyDataSetChanged();
        saveHistory();
    }

    private void saveHistory() {
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(historyList);
        editor.putStringSet(HISTORY_KEY, set);
        editor.apply();

    }

    private void loadHistory() {
        Set<String> set = prefs.getStringSet(HISTORY_KEY, new HashSet<>());
        historyList.addAll(set);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadExecutor.shutdown();
    }

}