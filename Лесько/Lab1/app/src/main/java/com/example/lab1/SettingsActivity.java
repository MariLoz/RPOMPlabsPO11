package com.example.lab1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText editTextServerUrl, editTextRowCount;
    private Button buttonSaveSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextServerUrl = findViewById(R.id.editTextServerUrl);
        editTextRowCount = findViewById(R.id.editTextRowCount);
        buttonSaveSettings = findViewById(R.id.buttonSaveSettings);

        buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSettings();
            }
        });

        LoadSettings();
    }

    private void SaveSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("serverUrl", editTextServerUrl.getText().toString());
        editor.putInt("rowCount", Integer.parseInt(editTextRowCount.getText().toString()));
        editor.apply();
        finish();
    }

    private void LoadSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        editTextServerUrl.setText(sharedPreferences.getString("serverUrl", "https://restcountries.com/v3.1/lang/russian"));
        editTextRowCount.setText(String.valueOf(sharedPreferences.getInt("rowCount", 10)));
    }
}