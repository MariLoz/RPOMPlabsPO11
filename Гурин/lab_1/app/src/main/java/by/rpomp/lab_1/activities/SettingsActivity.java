package by.rpomp.lab_1.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import by.rpomp.lab_1.MyApplication;
import by.rpomp.lab_1.R;

public class SettingsActivity extends AppCompatActivity {
    private MyApplication myApplication;
    private EditText apodAPI;
    private EditText marsRoverImagesAPI;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myApplication = (MyApplication) getApplicationContext();
        apodAPI = findViewById(R.id.apodAPI);
        marsRoverImagesAPI = findViewById(R.id.marsRoverPhotosAPI);
        saveButton = findViewById(R.id.saveButton);

        marsRoverImagesAPI.setText(myApplication.mrpUrl);
        apodAPI.setText(myApplication.apodUrl);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mrp = marsRoverImagesAPI.getText().toString();
                String apod = apodAPI.getText().toString();
                myApplication.mrpUrl = mrp;
                myApplication.apodUrl = apod;
                finish();
            }
        });
    }
}
