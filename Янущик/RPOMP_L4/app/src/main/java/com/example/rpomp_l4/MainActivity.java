package com.example.rpomp_l4;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText etInput;
    private Button btnDownload, btnView, btnDelete, btnShowHint, btnCustomPopup;
    private RadioGroup rgDownloadMode;
    private RadioButton rbNTV, rbCustomURL;
    private File storageDir, customStorageDir;
    private TextView tvCustomPopupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = findViewById(R.id.etInput);
        btnDownload = findViewById(R.id.btnDownload);
        btnView = findViewById(R.id.btnView);
        btnDelete = findViewById(R.id.btnDelete);
        btnShowHint = findViewById(R.id.btnShowHint);
        btnCustomPopup = findViewById(R.id.btnCustomPopup);
        rgDownloadMode = findViewById(R.id.rgDownloadMode);
        rbNTV = findViewById(R.id.rbNTV);
        rbCustomURL = findViewById(R.id.rbCustomURL);

        // Создание папок для хранения файлов
        storageDir = new File(getExternalFilesDir(null), "NTVDownloads");
        customStorageDir = new File(getExternalFilesDir(null), "CustomDownloads");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        if (!customStorageDir.exists()) {
            customStorageDir.mkdirs();
        }

        // Проверка настроек для показа всплывающего окна
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean dontShowAgain = preferences.getBoolean("dontShowAgain", false);

        if (!dontShowAgain) {
            // Отложенный вызов showPopupWindow()
            findViewById(android.R.id.content).post(this::showPopupWindow);
        }

        // Обработка выбора режима загрузки
        rgDownloadMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbNTV) {
                etInput.setHint("Введите ID журнала");
            } else {
                etInput.setHint("Введите URL");
            }
            // Проверяем существование файла и обновляем кнопки
            checkFileAndUpdateButtons();
        });

        // Проверяем существование файла при запуске
        checkFileAndUpdateButtons();

        // Кнопка для показа подсказки
        btnShowHint.setOnClickListener(v -> {
            SharedPreferences preferences1 = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putBoolean("dontShowAgain", false); // Сбрасываем настройку
            editor.apply();
            showPopupWindow(); // Показываем подсказку
        });

        // Кнопка для показа всплывающего окна с текстом
        btnCustomPopup.setOnClickListener(v -> showCustomPopupWindow());

        btnDownload.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                String input = etInput.getText().toString();
                if (!input.isEmpty()) {
                    String fileUrl;
                    File downloadDir;
                    if (rbNTV.isChecked()) {
                        // Режим NTV
                        fileUrl = "https://ntv.ifmo.ru/file/journal/" + input + ".pdf";
                        downloadDir = storageDir;
                    } else {
                        // Режим Custom URL
                        fileUrl = input;
                        downloadDir = customStorageDir;
                    }
                    Log.d("DownloadURL", "Downloading from: " + fileUrl);
                    new DownloadFileTask(downloadDir).execute(fileUrl);
                } else {
                    Toast.makeText(this, "Введите данные", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_SHORT).show();
            }
        });

        btnView.setOnClickListener(v -> {
            File currentFile;
            if (rbNTV.isChecked()) {
                currentFile = new File(storageDir, "journal.pdf");
            } else {
                currentFile = new File(customStorageDir, "journal.pdf");
            }

            if (currentFile.exists()) {
                // Создаем Intent для открытия PDF
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        FileProvider.getUriForFile(this, getPackageName() + ".provider", currentFile),
                        "application/pdf"
                );
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Нет приложения для открытия PDF", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            File currentFile;
            if (rbNTV.isChecked()) {
                currentFile = new File(storageDir, "journal.pdf");
            } else {
                currentFile = new File(customStorageDir, "journal.pdf");
            }

            if (currentFile.exists() && currentFile.delete()) {
                Toast.makeText(this, "Файл удален", Toast.LENGTH_SHORT).show();
                checkFileAndUpdateButtons(); // Обновляем состояние кнопок
            } else {
                Toast.makeText(this, "Ошибка при удалении файла", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkFileAndUpdateButtons() {
        File currentFile;
        if (rbNTV.isChecked()) {
            currentFile = new File(storageDir, "journal.pdf");
        } else {
            currentFile = new File(customStorageDir, "journal.pdf");
        }

        if (currentFile.exists()) {
            btnView.setEnabled(true);
            btnDelete.setEnabled(true);
        } else {
            btnView.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showPopupWindow() {
        // Используем post() для отложенного выполнения
        findViewById(android.R.id.content).post(() -> {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_layout, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            CheckBox dontShowAgain = popupView.findViewById(R.id.dontShowAgain);
            Button btnOk = popupView.findViewById(R.id.btnOk);

            btnOk.setOnClickListener(v -> {
                if (dontShowAgain.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("dontShowAgain", true);
                    editor.apply();
                }
                popupWindow.dismiss();
            });

            popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
        });
    }

    private void showCustomPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.custom_popup_layout, null);

        tvCustomPopupText = popupView.findViewById(R.id.tvCustomPopupText);
        tvCustomPopupText.setText("Лабораторная работа № 13. Хранение данных. Настройки и внешние файлы.\n" +
                "Выполнил: Янущик Д.Д. ПО-11\n" +
                "Задание на лабораторную работу\n" +
                "Задание 1. Изучите пример подключения к сети.\n" +
                "public void myClickHandler(View view) { \n" +
                "... \n" +
                "ConnectivityManager connMgr = (ConnectivityManager) \n" +
                "getSystemService(Context.CONNECTIVITY_SERVICE);\n" +
                "NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();\n" +
                "if (networkInfo != null && networkInfo.isConnected()) {\n" +
                "// fetch data \n" +
                "} else { \n" +
                "// display error \n" +
                "} \n" +
                "... \n" +
                "}\n" +
                "Задание 2. Изучите код приложений\n" +
                "HttpURLConnection\n" +
                "private String downloadUrl(String myurl) throws IOException { \n" +
                "InputStream is = null; \n" +
                "// Only display the first 500 characters of the retrieved \n" +
                "// web page content. \n" +
                "int len = 500; \n" +
                "try { \n" +
                "URL url = new URL(myurl); \n" +
                "HttpURLConnection conn = (HttpURLConnection) \n" +
                "url.openConnection();\n" +
                "23\n" +
                "conn.setReadTimeout(10000 /* milliseconds */);\n" +
                "conn.setConnectTimeout(15000 /* milliseconds */);\n" +
                "conn.setRequestMethod(\"GET\"); conn.setDoInput(true); \n" +
                "// Starts the query conn.connect(); \n" +
                "int response = conn.getResponseCode(); \n" +
                "Log.d(DEBUG_TAG, \"The response is: \" + response); is = \n" +
                "conn.getInputStream(); \n" +
                "// Convert the InputStream into a string \n" +
                "String contentAsString = readIt(is, len); \n" +
                "return contentAsString; \n" +
                "// Makes sure that the InputStream is closed after the app is \n" +
                "// finished using it. \n" +
                "} finally { \n" +
                "if (is != null) { \n" +
                "is.close(); \n" +
                "} \n" +
                "} \n" +
                "}\n" +
                "Преобразование полученной информации к типу Srting\n" +
                "public String readIt (InputStream stream, int len) throws IOException, \n" +
                "UnsupportedEncodingException { \n" +
                "Reader reader = null; \n" +
                "reader = new InputStreamReader(stream, \"UTF-8\"); \n" +
                "char[] buffer = new char[len]; \n" +
                "reader.read(buffer); \n" +
                "return new String(buffer); \n" +
                "}\n" +
                "Http GET запрос\n" +
                "• Создаем HttpClient\n" +
                "HttpClient client = new DefaultHttpClient();\n" +
                "• Создаем объект HttpGet\n" +
                "HttpGet request = new HttpGet(\"http://www.example.com\");\n" +
                "• Выполняем HTTP запрос\n" +
                "HttpResponse response;\n" +
                "try {\n" +
                "response = client.execute(request);\n" +
                "Log.d(\"Response of GET request\", response.toString());\n" +
                "} catch (ClientProtocolException e) {\n" +
                "// TODO Auto-generated catch block\n" +
                "e.printStackTrace();\n" +
                "} catch (IOException e) {\n" +
                "24\n" +
                "// TODO Auto-generated catch block\n" +
                "e.printStackTrace();\n" +
                "}\n" +
                "Взаимодействие с сервером через сокеты\n" +
                "public class Requester extends Thread {\n" +
                "Socket requestSocket;\n" +
                "String message;\n" +
                "StringBuilder returnStringBuffer = new StringBuilder();\n" +
                "Message lmsg;\n" +
                "int ch;\n" +
                "@Override public void run() {\n" +
                "try {\n" +
                "this.requestSocket = new Socket(\"remote.servername.com\", \n" +
                "13);\n" +
                "InputStreamReader isr = new \n" +
                "InputStreamReader(this.requestSocket. getInputStream(), \"ISO-8859-\n" +
                "1\");\n" +
                "while ((this.ch = isr.read()) != -1) {\n" +
                "this.returnStringBuffer.append((char) this.ch);\n" +
                "}\n" +
                "this.message = this.returnStringBuffer.toString();\n" +
                "this.lmsg = new Message();\n" +
                "this.lmsg.obj = this.message;\n" +
                "this.lmsg.what = 0;\n" +
                "h.sendMessage(this.lmsg);\n" +
                "this.requestSocket.close();\n" +
                "}\n" +
                "catch (Exception ee) {\n" +
                "Log.d(\"sample application\", \"failed to read data\" + \n" +
                "ee.getMessage());\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "Задание 3. Работа с внешними файлами.\n" +
                "Разработать мобильное приложение, позволяющее пользователю \n" +
                "асинхронно скачивать файлы журнала Научно-технический вестник. Файлы \n" +
                "хранятся на сервере в формате PDF и расположены по адресу:\n" +
                "http://ntv.ifmo.ru/file/journal/идентификатор_журнала.pdf\n" +
                "Не для всех ID имеются журналы, поэтому необходимо предусмотреть \n" +
                "сообщение об отсутствии файла. В случае если файл не найден, ответ от \n" +
                "сервера будет содержать главную страницу сайта.\n" +
                "25\n" +
                "Определить существует ли файл можно по возвращаемому сервером \n" +
                "заголовку (параметр content-type).\n" +
                "Примеры ссылок:\n" +
                "http://ntv.ifmo.ru/file/journal/1.pdf – возвращен PDF файл\n" +
                "http://ntv.ifmo.ru/file/journal/2.pdf – файл не найден, возвращена главная \n" +
                "страница сайта\n" +
                "Файлы должны храниться на устройстве в папке, создаваемой при \n" +
                "первом запуске приложения (путь до папки и ее название определите \n" +
                "самостоятельно).\n" +
                "После окончания загрузки файла должна становиться доступной \n" +
                "кнопка «Смотреть» и кнопка «Удалить».\n" +
                "При нажатии на кнопку «Смотреть» должно происходить открытие \n" +
                "сохраненного на устройстве файла. Предусмотреть ошибку, если на \n" +
                "устройстве не установлено приложение, открывающее PDF файлы.\n" +
                "При нажатии на кнопку «Удалить» загруженный файл должен \n" +
                "удаляться с устройства.\n" +
                "Задание 4. Хранение и чтение настроек.\n" +
                "При запуске приложения пользователю должно выводиться \n" +
                "всплывающее полупрозрачное уведомление (popupWindow), с краткой \n" +
                "инструкцией по использованию приложения (можете написать случайный \n" +
                "текст), чекбоксом «Больше не показывать» и кнопкой «ОК».\n" +
                "Если чекбокс был отмечен и нажата кнопка ОК, необходимо \n" +
                "произвести сохранение данного параметра используя SharedPreferences. При \n" +
                "следующем запуске приложения производить проверку параметра, и не \n" +
                "выводить всплывающее сообщение, если чекбокс был отмечен.");

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        Button btnCloseCustomPopup = popupView.findViewById(R.id.btnCloseCustomPopup);
        btnCloseCustomPopup.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    private class DownloadFileTask extends AsyncTask<String, Void, File> {
        private File downloadDir;

        public DownloadFileTask(File downloadDir) {
            this.downloadDir = downloadDir;
        }

        @Override
        protected File doInBackground(String... urls) {
            String url = urls[0];
            File downloadedFile = new File(downloadDir, "journal.pdf");

            try {
                URL fileUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    FileOutputStream output = new FileOutputStream(downloadedFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }

                    output.close();
                    input.close();
                    return downloadedFile;
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.e("DownloadFileTask", "Ошибка загрузки файла", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (file != null) {
                Toast.makeText(MainActivity.this, "Файл загружен", Toast.LENGTH_SHORT).show();
                checkFileAndUpdateButtons(); // Обновляем состояние кнопок
            } else {
                Toast.makeText(MainActivity.this, "Файл не найден", Toast.LENGTH_SHORT).show();
            }
        }
    }
}