package com.example.lab1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class LiteratureDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_literature_detail);

        Button backButton = findViewById(R.id.back_button);

        // Получаем данные из Intent
        String name = getIntent().getStringExtra("name");
        String info = getIntent().getStringExtra("info");
        String bookUrl = getIntent().getStringExtra("bookUrl");

        // Находим View и устанавливаем данные
        TextView nameTextView = findViewById(R.id.detail_name);
        TextView infoTextView = findViewById(R.id.detail_info);
        ImageView bookImageView = findViewById(R.id.detail_book);

        backButton.setOnClickListener(v -> {
            finish(); // Закрывает текущую активность, возвращаясь к предыдущей
        });

        nameTextView.setText(name);
        infoTextView.setText(info);
        Glide.with(this).load(bookUrl).into(bookImageView);
    }
}
