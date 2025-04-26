package com.example.media;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private boolean showingMediaHistory = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnMediaHistory).setOnClickListener(v -> {
            showingMediaHistory = true;
            loadHistory();
        });

        findViewById(R.id.btnPhotoHistory).setOnClickListener(v -> {
            showingMediaHistory = false;
            loadHistory();
        });

        findViewById(R.id.bBack).setOnClickListener(v -> finish());

        Button taskInfoButton = findViewById(R.id.taskInfoButton);
        Button authorButton = findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(this, "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        loadHistory();
    }

    private void loadHistory() {
        Cursor cursor = showingMediaHistory
                ? HistoryDatabase.getInstance(this).getMediaHistory()
                : HistoryDatabase.getInstance(this).getPhotoHistory();
        adapter = new HistoryAdapter(cursor);
        recyclerView.setAdapter(adapter);
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private Cursor cursor;

        public HistoryAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (cursor.moveToPosition(position)) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow("path"));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
                holder.text1.setText("Путь: " + path);
                if (showingMediaHistory) {
                    holder.text2.setText("Время: " + new java.util.Date(timestamp).toString());
                } else {
                    String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                    holder.text2.setText("Время: " + new java.util.Date(timestamp).toString() + "\nМестоположение: " + location);
                }
            }
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}