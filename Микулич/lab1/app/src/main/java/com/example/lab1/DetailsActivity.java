package com.example.lab1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private ImageView imageDetail;
    private TextView textName, textDescription, textPower, textSize, textYear;
    private Button btnPrev, btnExit, btnNext;

    private List<Phones> ampList;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageDetail = findViewById(R.id.imageDetail);
        textName = findViewById(R.id.textName);
        textDescription = findViewById(R.id.textDescription);
        textPower = findViewById(R.id.textPower);
        textSize = findViewById(R.id.textSize);
        textYear = findViewById(R.id.textYear);
        btnPrev = findViewById(R.id.btnPrev);
        btnExit = findViewById(R.id.btnExit);
        btnNext = findViewById(R.id.btnNext);

        // Получаем переданные данные
        ampList = (List<Phones>) getIntent().getSerializableExtra("ampList");
        currentIndex = getIntent().getIntExtra("index", 0);

        // Отображаем первый объект
        showDetails(currentIndex);

        btnPrev.setOnClickListener(v -> showPrevious());
        btnNext.setOnClickListener(v -> showNext());
        btnExit.setOnClickListener(v -> finish());
    }

    private void showDetails(int index) {
        if (ampList != null && index >= 0 && index < ampList.size()) {
            Phones amp = ampList.get(index);

            Glide.with(this)
                    .load(amp.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(imageDetail);

            textName.setText(amp.getName());
            textDescription.setText("Описание: " + amp.getDescription());
            textPower.setText("Мощность: " + amp.getPower() + " Вт");
            textSize.setText("Размер: " + amp.getSize() + " см");
            textYear.setText("Динамик: " + amp.getYear());
        }
    }



    private void showNext() {
        if (currentIndex < ampList.size() - 1) {
            currentIndex++;
            showDetails(currentIndex);
        } else {
            Toast.makeText(this, "Вы достигли конца списка!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            showDetails(currentIndex);
        } else {
            Toast.makeText(this, "Вы находитесь в начале списка!", Toast.LENGTH_SHORT).show();
        }
    }
}
