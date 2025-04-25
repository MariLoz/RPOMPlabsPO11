package com.example.laba4;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    //https://irozk.ru/images/doc/pdf_primer.pdf
    //https://ntv.ifmo.ru/file/journal/1.pdf

    private EditText etInput;
    private Button btnDownload, btnView, btnDelete, btnShowHint;
    private File storageDir;

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

        etInput = findViewById(R.id.etInput);
        btnDownload = findViewById(R.id.btnDownload);
        btnView = findViewById(R.id.btnView);
        btnDelete = findViewById(R.id.btnDelete);
        btnShowHint = findViewById(R.id.btnShowHint);

        storageDir = new File(getExternalFilesDir(null), "Downloads");
        if (!storageDir.exists()) storageDir.mkdirs();

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean dontShowAgain = preferences.getBoolean("dontShowAgain", false);

        if (!dontShowAgain) {
            findViewById(android.R.id.content).post(() -> showPopupWindow(false));
        }

        btnDownload.setOnClickListener(v -> downloadFile());
        btnView.setOnClickListener(v -> viewFile());
        btnDelete.setOnClickListener(v -> deleteFile());
        btnShowHint.setOnClickListener(v -> showPopupWindow(true));
    }

    private void showPopupWindow(boolean isManual) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        CheckBox dontShowAgain = popupView.findViewById(R.id.dontShowAgain);
        Button btnOk = popupView.findViewById(R.id.btnOk);

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean("checkboxState", false);
        dontShowAgain.setChecked(isChecked);

        btnOk.setOnClickListener(v -> {
            if (dontShowAgain.isChecked()) {
                SharedPreferences.Editor editor = preferences.edit();
                if (!isManual) {
                    editor.putBoolean("dontShowAgain", true);
                }
                editor.putBoolean("checkboxState", true);
                editor.apply();
            }
            popupWindow.dismiss();
        });
    }

    private void downloadFile() {
        if (isNetworkAvailable()) {
            String url = etInput.getText().toString().trim();
            if (!url.isEmpty() && url.endsWith(".pdf")) {
                new DownloadTask(this, storageDir).execute(url);
            } else {
                showToast("Введите корректный URL PDF-файла");
            }
        } else {
            showToast("Нет подключения к интернету");
        }
    }

    private void viewFile() {
        File file = new File(storageDir, "downloaded_file.pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(
                    FileProvider.getUriForFile(this, getPackageName() + ".provider", file),
                    "application/pdf"
            );
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (Exception e) {
                showToast("Нет приложения для открытия PDF");
            }
        } else {
            showToast("Файл не найден");
        }
    }

    private void deleteFile() {
        File file = new File(storageDir, "downloaded_file.pdf");
        if (file.exists() && file.delete()) {
            showToast("Файл удален");
            enableButtons(false);
        } else {
            showToast("Ошибка при удалении файла");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void enableButtons(boolean enabled) {
        btnView.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
    }
}