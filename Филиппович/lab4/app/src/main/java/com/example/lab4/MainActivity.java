package com.example.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText bookTitle;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация элементов интерфейса
        bookTitle = findViewById(R.id.bookTitle);
        downloadButton = findViewById(R.id.downloadButton);

        // Отображение всплывающего окна с инструкцией
        findViewById(R.id.mainLayout).post(this::showFullScreenPopup);

        // Обработчик кнопки скачивания
        downloadButton.setOnClickListener(v -> {
            String title = bookTitle.getText().toString();
            if (!title.isEmpty()) {
                // Передаем название книги в DownloadActivity
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                intent.putExtra("BOOK_TITLE", title);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Введите название книги", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Отображение PopupWindow с инструкцией
    private void showFullScreenPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        Button okButton = popupView.findViewById(R.id.okButton);
        okButton.setOnClickListener(v -> popupWindow.dismiss());

        popupWindow.showAtLocation(findViewById(R.id.mainLayout), Gravity.CENTER, 0, 0);
    }
}
