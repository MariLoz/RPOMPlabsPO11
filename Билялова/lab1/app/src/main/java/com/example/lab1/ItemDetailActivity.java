package com.example.lab1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView title, header, description;
    private ImageView image;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        header = findViewById(R.id.header_text);
        header.setText("Билялова Александра, ПО-11");

        title = findViewById(R.id.detail_title);
        description = findViewById(R.id.detail_description);
        image = findViewById(R.id.detail_image);
        backButton = findViewById(R.id.back_button);

        String titleText = getIntent().getStringExtra("title");
        String descriptionText = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        title.setText(titleText);
        description.setText(descriptionText);
        Glide.with(this).load(imageUrl).into(image);

        backButton.setOnClickListener(v -> finish());
    }
}
