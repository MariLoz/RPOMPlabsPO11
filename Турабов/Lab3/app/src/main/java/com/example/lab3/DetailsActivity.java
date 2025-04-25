package com.example.lab3;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle arguments = getIntent().getExtras();
        Note selected = (Note) arguments.getSerializable(Note.class.getSimpleName());
        ArrayList<String> subs = (ArrayList<String>) arguments.getSerializable("subs");

        TextView id = findViewById(R.id.details_id);
        id.setText(((Integer)selected.get_id()).toString());
        TextView title = findViewById(R.id.details_title);
        title.setText(selected.getNoteTitle());
        TextView author = findViewById(R.id.details_author);
        author.setText(selected.getAuthorName());
        TextView task = findViewById(R.id.details_task);
        task.setText(selected.getMainTask());

        TextView[] subsViews= {findViewById(R.id.details_sub1),findViewById(R.id.details_sub2),findViewById(R.id.details_sub3)};

        for(int i = 0; i < subs.size(); i++)
        {
            subsViews[i].setText(subs.get(i));
        }
    }
}