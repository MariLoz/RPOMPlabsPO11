package com.example.kozinlab1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import android.widget.Button;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private int currentId;
    private List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView detailedDescriptionTextView = findViewById(R.id.detailedDescriptionTextView); // Для подробного описания
        ImageView imageView = findViewById(R.id.imageView);
        Button buttonBackToMain = findViewById(R.id.buttonBackToMain);
        Button buttonForward = findViewById(R.id.buttonForward);
        Button buttonBackward = findViewById(R.id.buttonBackward);

        // Получаем данные из Intent
        currentId = getIntent().getIntExtra("id", 0); // Получаем id элемента
        items = (List<Item>) getIntent().getSerializableExtra("items"); // Получаем список элементов

        // Находим элемент с соответствующим id
        Item currentItem = getItemById(currentId);
        if (currentItem != null) {
            descriptionTextView.setText(currentItem.getDescription());
            detailedDescriptionTextView.setText(currentItem.getDetailedDescription());
            Glide.with(this)
                    .load(currentItem.getImageUrl())
                    .into(imageView);
        }

        // Обработчик нажатия на кнопку "На главную"
        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("back_to_main", true); // Можно добавить дополнительные параметры, если нужно
            setResult(RESULT_OK, intent);
            finish(); // Закрываем DetailActivity
        });

        // Обработчик нажатия на кнопку "Вперед"
        buttonForward.setOnClickListener(v -> {
            Item nextItem = getNextItem();
            if (nextItem != null) {
                currentId = nextItem.getId();
                updateDetails(nextItem);
            }
        });

        // Обработчик нажатия на кнопку "Назад"
        buttonBackward.setOnClickListener(v -> {
            Item prevItem = getPrevItem();
            if (prevItem != null) {
                currentId = prevItem.getId();
                updateDetails(prevItem);
            }
        });
    }

    // Метод для обновления данных на экране
    private void updateDetails(Item item) {
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView detailedDescriptionTextView = findViewById(R.id.detailedDescriptionTextView);
        ImageView imageView = findViewById(R.id.imageView);

        descriptionTextView.setText(item.getDescription());
        detailedDescriptionTextView.setText(item.getDetailedDescription());
        Glide.with(this).load(item.getImageUrl()).into(imageView);
    }

    // Получаем элемент по id
    private Item getItemById(int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    // Получить следующий элемент
    private Item getNextItem() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == currentId && i < items.size() - 1) {
                return items.get(i + 1);
            }
        }
        return null;
    }

    // Получить предыдущий элемент
    private Item getPrevItem() {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == currentId && i > 0) {
                return items.get(i - 1);
            }
        }
        return null;
    }
}
