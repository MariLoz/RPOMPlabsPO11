package com.example.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.minishop.adapters.checkedGoodsAdapter;
import com.example.minishop.models.Good;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    private ListView listView;
    private checkedGoodsAdapter checkedGoodsAdapter;
    private TextView tvItemCount;
    private Button taskInfoButton, authorButton;
    private ArrayList<Good> checkedGoods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvItemCount = findViewById(R.id.tv_item_count);
        listView = findViewById(R.id.listView);
        taskInfoButton = findViewById(R.id.taskInfoButton);
        authorButton = findViewById(R.id.authorButton);

        checkedGoods = getIntent().getParcelableArrayListExtra("MyList");
        if (checkedGoods != null) {
            int itemCount = checkedGoods.size();
            tvItemCount.setText("В вашей корзине " + itemCount + " товара(ов):");
            checkedGoodsAdapter = new checkedGoodsAdapter(this, checkedGoods);
            listView.setAdapter(checkedGoodsAdapter);
        } else {
            tvItemCount.setText("В вашей корзине 0 товара(ов):");
        }

        setupButtons();
    }

    private void setupButtons() {
        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());
    }
}