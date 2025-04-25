package com.example.listdisplay;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> showAboutDialog());
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("О задаче")
                .setMessage("Задача: Реализовать интерфейс приложения для отображения списка элементов. " +
                        "В качестве данных для списка использовать файл в формате json, загруженный с удаленного сервера. \n\n" +
                        "Выполнил: [Сильчук Д.А.]\n" +
                        "Группа: [ПО-11]")
                .setPositiveButton("OK", null)
                .show();
    }
}