package com.example.project2;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new ListFragment())
                        .commit();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка запуска приложения: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}