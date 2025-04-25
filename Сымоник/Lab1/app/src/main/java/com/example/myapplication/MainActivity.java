package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int CREATE_FILE_REQUEST_CODE = 1;
    private static final int OPEN_FILE_REQUEST_CODE = 2;
    private ArrayList<CharacterItem> characterList;
    private CharacterAdapter adapter;

    private String _jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView listView = findViewById(R.id.characterListView);
        characterList = new ArrayList<>();
        adapter = new CharacterAdapter(this, characterList);
        listView.setAdapter(adapter);

        loadData();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            CharacterItem item = characterList.get(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("character", item);
            Toast.makeText(MainActivity.this, "Нажал Сымоник И.А.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        ImageView settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this::showPopupMenu);
    }

    private void loadData() {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String jsonUrl = preferences.getString("json_url", "").trim();

        if (!jsonUrl.isEmpty()) {
            new LoadJsonTask().execute(jsonUrl);
        } else {
            loadCharacterDataFromAssets();
        }
    }

    private void loadCharacterDataFromAssets() {
        try {
            InputStream is = getAssets().open("characters.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            parseCharacterData(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class LoadJsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String jsonUrl = params[0];
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(jsonUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String json) {
            if (json != null) {
                Toast.makeText(MainActivity.this, "JSON загружен с сайта.", Toast.LENGTH_SHORT).show();
                parseCharacterData(json);
            } else {
                Toast.makeText(MainActivity.this, "Ошибка загрузки JSON. Используем локальный файл.", Toast.LENGTH_SHORT).show();
                loadCharacterDataFromAssets();
            }
        }
    }

    private void parseCharacterData(String json) {
        try {
            _jsonData = json;

            characterList.clear();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject characterObj = dataArray.getJSONObject(i);
                String name = characterObj.getString("name");
                String element = characterObj.getString("element");
                String weapon = characterObj.getString("weapon");
                String region = characterObj.getString("region");
                String thumbnail = characterObj.getString("thumbnail");
                String wikiUrl = characterObj.getString("link");
                int rarity = characterObj.getInt("rarity");

                JSONArray jsonTalents = characterObj.getJSONArray("talents");
                ArrayList<Talent> talents = new ArrayList<>();

                for (int j = 0; j < jsonTalents.length(); j++) {
                    JSONObject tal = jsonTalents.getJSONObject(j);
                    talents.add(new Talent(
                            tal.getString("name"),
                            tal.getString("thumbnail"),
                            tal.getString("link")
                    ));
                }

                JSONArray jsonConstel = characterObj.getJSONArray("constellations");
                ArrayList<Constellation> constellations = new ArrayList<>();

                for (int j = 0; j < jsonConstel.length(); j++) {
                    JSONObject con = jsonConstel.getJSONObject(j);
                    constellations.add(new Constellation(con.getString("name")));
                }

                characterList.add(new CharacterItem(name, element, weapon, region, thumbnail, wikiUrl, rarity, talents, constellations));
            }

            adapter.notifyDataSetChanged();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.settings_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.menu_exit) {
                Toast.makeText(this, "Выход из приложения", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            return false;
        });

        popup.show();
    }


}
