package com.example.minishop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.secondlab.R;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    private ListView listViewCart;
    private GoodsAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.secondlab.R.layout.activity_second);

        listViewCart = findViewById(R.id.listViewCart);
        ArrayList<Good> checkedGoods = getIntent().getParcelableArrayListExtra("MyList");
        if (checkedGoods != null && !checkedGoods.isEmpty()) {
            cartAdapter = new GoodsAdapter(this, checkedGoods, true); // Режим корзины
            listViewCart.setAdapter(cartAdapter);
        } else {
            listViewCart.setAdapter(new GoodsAdapter(this, new ArrayList<>(), true));
        }

        Button backButton = findViewById(R.id.btnShow);
        backButton.setOnClickListener(v -> {
            Toast.makeText(this, "Нажал Жватель!", Toast.LENGTH_SHORT).show();
            // Можно добавить finish() если хотите закрыть активность при нажатии
            // finish();
        });
    }
}