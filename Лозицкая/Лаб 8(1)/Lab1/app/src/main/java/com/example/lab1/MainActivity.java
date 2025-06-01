package com.example.lab1;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements ListFragment.OnFragmentSendDataListener {
    private ActivityResultLauncher<Intent> openFileLauncher;
    private ActivityResultLauncher<Intent> saveFileLauncher;
    private ActivityResultLauncher<Intent> settingsActivityLauncher;
    private JSONArray tanksJsonArray = new JSONArray();
    private String currentUrl = "https://api.worldoftanks.eu/wot/encyclopedia/vehicles/?application_id=80f4df6eb27bcc0459b275324f8cdc8e&language=en&fields=" +
            "name,nation,tier,type,default_profile.armor.hull.front,default_profile.gun.name,default_profile.speed_forward,price_credit";
    private int itemsPerPage = 5;
    private String selectedFileType = null;

    @SuppressLint("MissingInflatedId")
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                try {
                                    ContentResolver resolver = getContentResolver();
                                    InputStream inputStream = resolver.openInputStream(uri);

                                    File file = createTempFile(resolver, uri);

                                    assert inputStream != null;
                                    copyInputStreamToFile(inputStream, file);

                                    tanksJsonArray = getJsonArray(file);

                                    ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
                                    assert listFragment != null;
                                    listFragment.loadTanksFromJson(tanksJsonArray);
                                } catch (IOException | JSONException e) {
                                    Log.e("JSON", "Ошибка чтения JSON", e);
                                    Toast.makeText(this, "Ошибка чтения JSON", Toast.LENGTH_SHORT).show();
                                } catch (SecurityException e) {
                                    Log.e("JSON", "Ошибка доступа к файлу: " + e.getMessage(), e);
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

        saveFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                try {
                                    saveJsonArrayInFile(uri, tanksJsonArray, selectedFileType);
                                } catch (IOException e) {
                                    Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "URI сохранения отсутствует", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Данные отсутствуют", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Сохранение файла отменено", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        settingsActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String newUrl = data.getStringExtra("url");
                            itemsPerPage = data.getIntExtra("itemsPerPage", 5);
                            if (newUrl != null) {
                                currentUrl = newUrl;
                            }
                            loadListFragment(itemsPerPage);
                        }
                    }
                });

        loadListFragment(itemsPerPage);
    }

    private void loadListFragment(int itemsPerPage) {
        ListFragment listFragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("itemsPerPage", itemsPerPage);
        listFragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.listFragment, listFragment);
        transaction.commit();
    }

    public interface OnJsonLoadedListener {
        void onJsonLoaded(JSONArray jsonArray);
    }

    public void openJsonFileTanksFromUrl(View v) {
        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
        if (listFragment != null) {
            loadJsonFileFromUrl(currentUrl, new OnJsonLoadedListener() {
                @Override
                public void onJsonLoaded(JSONArray jsonArray) {
                    listFragment.loadTanksFromJson(jsonArray);
                }
            });
        }
    }

    public void loadJsonFileFromUrl(String url, OnJsonLoadedListener listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ERROR", "Ошибка загрузки: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка загрузки!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("ERROR", "Ошибка ответа сервера: " + response.code());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка ответа сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                    return;
                }

                assert response.body() != null;
                String jsonData = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject data = jsonObject.getJSONObject("data");
                    tanksJsonArray = createTankJsonArray(data);

                    runOnUiThread(() -> {
                        listener.onJsonLoaded(tanksJsonArray);
                    });

                } catch (JSONException e) {
                    Log.e("ERROR", "Ошибка парсинга JSON: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка парсинга JSON!", Toast.LENGTH_SHORT).show());
                }
            }

            @NonNull
            public JSONArray createTankJsonArray(@NonNull JSONObject data) throws JSONException {
                JSONArray tanksJsonArray = new JSONArray();
                Iterator<String> keys = data.keys();

                int count = 0;
                while (keys.hasNext() && count < 27) {
                    String key = keys.next();
                    JSONObject tank = data.getJSONObject(key);
                    JSONObject tankInfo = new JSONObject();

                    tankInfo.put("name", tank.getString("name"));
                    tankInfo.put("nation", tank.getString("nation"));
                    tankInfo.put("level", tank.getInt("tier"));
                    tankInfo.put("type", tank.getString("type"));
                    tankInfo.put("armor", tank.getJSONObject("default_profile").getJSONObject("armor").getJSONObject("hull").getInt("front"));
                    tankInfo.put("gun", tank.getJSONObject("default_profile").getJSONObject("gun").getString("name"));
                    tankInfo.put("speed", tank.getJSONObject("default_profile").getInt("speed_forward"));
                    tankInfo.put("cost", tank.optInt("price_credit", 0));
                    tankInfo.put("photo", tank.getString("name").toLowerCase().replace(" ", "").replace("-","").replace(".","") + ".png");

                    tanksJsonArray.put(tankInfo);
                    count++;
                }
                return tanksJsonArray;
            }
        });
    }

    @Override
    public void onSendData(Tank selectedTank) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.SELECTED_TANK, selectedTank);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.open_file) {
            openFilePicker();
        } else if (id == R.id.download_file) {
            showFileTypeSelectionDialog();
        } else if (id == R.id.clear_list) {
            clearTankList();
        } else if (id == R.id.change_url) {
            Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra("currentUrl", currentUrl);
            intent.putExtra("currentItemsPerPage", itemsPerPage);
            settingsActivityLauncher.launch(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            openFileLauncher.launch(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Пожалуйста, установите файловый менеджер.", Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private File createTempFile(@NonNull ContentResolver resolver, Uri uri) throws IOException {
        Cursor cursor = resolver.query(uri, null, null, null, null);
        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            name = cursor.getString(nameIndex);
            cursor.close();
        }

        File tempFile = File.createTempFile("temp_", "_" + name, getCacheDir());
        tempFile.deleteOnExit();
        return tempFile;
    }

    private void copyInputStreamToFile(@NonNull InputStream inputStream, File file) throws IOException {
        try (inputStream; FileOutputStream outputStream = new FileOutputStream(file)) {
            int read;
            byte[] buffer = new byte[4096];
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }
    }

    @NonNull
    public static JSONArray getJsonArray(File file) throws IOException, JSONException {
        StringBuilder stringBuilder = new StringBuilder();

        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }

        String jsonString = stringBuilder.toString();
        try {
            return new JSONArray(jsonString);
        } catch (JSONException e) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                return jsonArray;
            } catch (JSONException e2) {
                throw new JSONException("Не удалось распарсить JSON: " + e2.getMessage());
            }
        }
    }

    private void showFileTypeSelectionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Выберите тип файла")
                .setItems(new CharSequence[]{"JSON", "TXT"}, (dialog, which) -> {
                    String fileType;
                    if (which == 0) { fileType = "json"; }
                    else { fileType = "txt"; }
                    createFile(fileType);
                })
                .show();
    }

    private void createFile(@NonNull String fileType) {
        selectedFileType = fileType;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        String fileName;
        if (fileType.equals("json")) {
            intent.setType("application/json");
            fileName = "tanks.json";
        } else {
            intent.setType("text/plain");
            fileName = "tanks.txt";
        }

        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        saveFileLauncher.launch(intent);
    }

    private void saveJsonArrayInFile(Uri uri, JSONArray jsonArray, @NonNull String fileType) throws IOException {
        if (fileType.equals("json")) {
            String jsonData = jsonArray.toString();
            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    byte[] data = jsonData.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(data);
                    Toast.makeText(this, "Файл JSON успешно сохранен", Toast.LENGTH_SHORT).show();
                    Log.d("FileSaver", "Файл JSON успешно сохранен по URI: " + uri.toString());
                } else {
                    Log.e("FileSaver", "Не удалось открыть OutputStream");
                    throw new IOException("Не удалось открыть OutputStream");
                }
            }
        } else if (fileType.equals("txt")) {
            StringBuilder textData = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject tank = jsonArray.getJSONObject(i);
                    textData.append("Name: ").append(tank.getString("name")).append("\n");
                    textData.append("Nation: ").append(tank.getString("nation")).append("\n");
                    textData.append("Level: ").append(tank.getInt("level")).append("\n");
                    textData.append("Type: ").append(tank.getString("type")).append("\n");
                    textData.append("Armor: ").append(tank.getInt("armor")).append("\n");
                    textData.append("Gun: ").append(tank.getString("gun")).append("\n");
                    textData.append("Speed: ").append(tank.getInt("speed")).append("\n");
                    textData.append("Cost: ").append(tank.optInt("cost", 0)).append("\n");
                    textData.append("\n");
                } catch (JSONException e) {
                    Log.e("FileSaver", "Ошибка преобразования JSON в текст", e);
                    throw new IOException("Ошибка преобразования JSON в текст", e);
                }
            }

            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    byte[] data = textData.toString().getBytes(StandardCharsets.UTF_8);
                    outputStream.write(data);
                    Toast.makeText(this, "Файл TXT успешно сохранен", Toast.LENGTH_SHORT).show();
                    Log.d("FileSaver", "Файл TXT успешно сохранен по URI: " + uri.toString());
                } else {
                    Log.e("FileSaver", "Не удалось открыть OutputStream");
                    throw new IOException("Не удалось открыть OutputStream");
                }
            }
        } else {
            throw new IOException("Неподдерживаемый тип файла: " + fileType);
        }
    }

    private void clearTankList() {
        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);
        if (listFragment != null) {
            listFragment.clearTanks();
        } else {
            Toast.makeText(this, "Не удалось очистить список танков", Toast.LENGTH_SHORT).show();
        }
    }
}