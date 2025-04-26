package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener btnClick = v -> Click(v.getId());

        ((ImageButton) findViewById(R.id.bMusic)).setOnClickListener(btnClick);
        ((ImageButton) findViewById(R.id.bCamera)).setOnClickListener(btnClick);
        ((ImageButton) findViewById(R.id.bGallery)).setOnClickListener(btnClick);
        ((ImageButton) findViewById(R.id.bHelp)).setOnClickListener(btnClick);
        ((ImageButton) findViewById(R.id.bHistory)).setOnClickListener(btnClick);

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

    }

    protected void Click(int viewId) {
        Intent intent = null;
        if (viewId == R.id.bMusic) {
            intent = new Intent(this, MediaActivity.class);
        } else if (viewId == R.id.bGallery) {
            intent = new Intent(this, GalleryActivity.class);
        } else if (viewId == R.id.bCamera) {
            intent = new Intent(this, CameraActivity.class);
        } else if (viewId == R.id.bHelp) {
            intent = new Intent(this, HelpActivity.class);
        } else if (viewId == R.id.bHistory) {
            intent = new Intent(this, HistoryActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}