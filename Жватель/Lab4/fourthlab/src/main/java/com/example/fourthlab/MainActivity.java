package com.example.fourthlab;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

public class MainActivity extends Activity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_SHOW_POPUP = "showPopup";
    private static final String FILE_NAME = "journal.pdf";
    private Uri downloadedFileUri;
    private EditText journalIdInput;
    private Button infoButton; // Новая кнопка для отображения информации о задаче

    // Информация о задании и авторе
    private static final String TASK_DESCRIPTION = "Лабораторная работа №13.\n" +
            "Задание 1. Реализуйте пример подключения к сети.\n" +
            "Задание 2. Реализуйте коды приложений в примерах из источника (запросы, взаимодействие с сервером через сокеты).\n" +
            "Задание 3. Разработайте мобильное приложение согласно заданию 3 источника, позволяющее пользователю асинхронно скачивать файлы журнала Научно-технический вестник (возможно взять другой источник файлов подобной структуры).\n" +
            "Задание 4. Хранение и чтение настроек. При запуске приложения пользователю должно выводиться всплывающее полупрозрачное уведомление (popupWindow), с краткой инструкцией по использованию приложения, чекбоксом «Больше не показывать» и кнопкой «ОК».\n" +
            "Бонус: Использование собственного источника документов.";
    private static final String AUTHOR_INFO = "Выполнил: Жватель Станислав Сергеевич\n" +
            "Группа: ПО-11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        journalIdInput = findViewById(R.id.journal_id_input);

        Button downloadButton = findViewById(R.id.download_button);
        Button viewButton = findViewById(R.id.view_button);
        Button deleteButton = findViewById(R.id.delete_button);
        infoButton = findViewById(R.id.infoButton); // Инициализация кнопки информации
        Button pressButton = findViewById(R.id.pressButton); // Инициализация кнопки "Нажимает Жватель"

        downloadButton.setOnClickListener(v -> downloadFile());
        viewButton.setOnClickListener(v -> viewFile());
        deleteButton.setOnClickListener(v -> deleteFile());
        infoButton.setOnClickListener(v -> showTaskInfo()); // Обработчик для кнопки информации
        pressButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Жватель!", Toast.LENGTH_SHORT).show()); // Обработчик для кнопки "Нажимает Жватель"

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean showPopup = prefs.getBoolean(KEY_SHOW_POPUP, true);

        if (showPopup) {
            findViewById(R.id.main_layout).post(() -> showPopupInstruction());
        }
    }

    private void downloadFile() {
        String journalId = journalIdInput.getText().toString().trim();
        if (journalId.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, введите ID журнала", Toast.LENGTH_LONG).show();
            return;
        }

        new DownloadTask(this) {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.startsWith("Download completed")) {
                    String uriString = result.split(" ")[2];
                    downloadedFileUri = Uri.parse(uriString);
                    Toast.makeText(MainActivity.this, "Файл успешно скачан", Toast.LENGTH_LONG).show();
                } else if (!result.equals("redirect_to_main")) {
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(journalId);
    }

    private void viewFile() {
        if (downloadedFileUri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadedFileUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Не удалось открыть файл: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Файл не найден. Сначала скачайте его.", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteFile() {
        if (downloadedFileUri != null) {
            try {
                ContentResolver resolver = getContentResolver();
                int deletedRows = resolver.delete(downloadedFileUri, null, null);
                if (deletedRows > 0) {
                    Toast.makeText(this, "Файл удален", Toast.LENGTH_LONG).show();
                    downloadedFileUri = null;
                } else {
                    Toast.makeText(this, "Не удалось удалить файл", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка при удалении: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Файл не найден", Toast.LENGTH_LONG).show();
        }
    }

    private void showPopupInstruction() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        View anchorView = findViewById(R.id.main_layout);
        if (anchorView == null) {
            return;
        }

        TextView instructionText = popupView.findViewById(R.id.instruction_text);
        instructionText.setText("Добро пожаловать! Введите ID журнала и нажмите 'Скачать' для загрузки файла.");

        final CheckBox dontShowAgain = popupView.findViewById(R.id.dont_show_again);
        Button okButton = popupView.findViewById(R.id.ok_button);

        okButton.setOnClickListener(v -> {
            if (dontShowAgain.isChecked()) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_SHOW_POPUP, false);
                editor.apply();
            }
            popupWindow.dismiss();
        });

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void showTaskInfo() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        View anchorView = findViewById(R.id.main_layout);
        if (anchorView == null) {
            return;
        }

        TextView instructionText = popupView.findViewById(R.id.instruction_text);
        instructionText.setText(TASK_DESCRIPTION + "\n\n" + AUTHOR_INFO);

        final CheckBox dontShowAgain = popupView.findViewById(R.id.dont_show_again);
        dontShowAgain.setVisibility(View.GONE); // Скрываем чекбокс для информации о задании
        Button okButton = popupView.findViewById(R.id.ok_button);

        okButton.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }
}