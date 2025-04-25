package com.example.lab5;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private Button videoButton;
    private Button audioButton;
    private Button imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoButton = findViewById(R.id.selectVideoButton);
        audioButton = findViewById(R.id.selectAudioButton);
        imageButton = findViewById(R.id.selectImageButton);


        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Нажал Сымоник И.А.", LENGTH_SHORT);
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, 1001);
            }
        });

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Нажал Сымоник И.А.", LENGTH_SHORT);

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT , android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                startActivityForResult(intent, 1002);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Нажал Сымоник И.А.", LENGTH_SHORT);

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, 1003);
            }
        });
    }

    // Обработка результата выбора файла
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedUri = data.getData();

            if (requestCode == 1001) {
                loadFragment(new VideoFragment(selectedUri));
            } else if (requestCode == 1002) {
                loadFragment(new AudioFragment(selectedUri));
            } else if (requestCode == 1003) {
                loadFragment(new ImageFragment(selectedUri));
            }
        }
    }

    // Метод для замены фрагмента
    private void loadFragment(androidx.fragment.app.Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
