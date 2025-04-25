package com.example.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DifficultySelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        int[] buttonIds = {R.id.easyButton, R.id.mediumButton, R.id.hardButton};
        GameActivity.Difficulty[] difficulties = GameActivity.Difficulty.values();

        for (int i = 0; i < buttonIds.length; i++) {
            Button btn = findViewById(buttonIds[i]);
            GameActivity.Difficulty diff = difficulties[i];

            btn.setOnClickListener(v -> {
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("DIFFICULTY", diff.name());
                startActivity(intent);
            });

            String description = String.format(
                    "• %d попыток\n• %s\n• %s",
                    diff.maxAttempts,
                    diff.timeLimit > 0 ? "Лимит времени: " + (diff.timeLimit/1000) + " сек" : "Без лимита",
                    diff.showRangeHint ? "С подсказками" : "Без подсказок"
            );

            TextView descText = findViewById(getResources()
                    .getIdentifier("descText"+(i+1), "id", getPackageName()));
            descText.setText(description);
        }
    }
}