package com.example.lab8;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class RunHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHelper databaseHelper;
    private List<Run> runList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_history);

        listView = findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);

        loadRuns();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Run selectedRun = runList.get(position);
            Intent intent = new Intent(RunHistoryActivity.this, RunDetailsActivity.class);
            intent.putExtra("run", selectedRun);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Run runToDelete = runList.get(position);

            String message = getString(R.string.delete_run_message, runToDelete.getTimestamp()) +
                    "\n\nDistance: " + runToDelete.getFormattedDistance() +
                    "\nDuration: " + runToDelete.getFormattedDuration();

            new AlertDialog.Builder(RunHistoryActivity.this, R.style.AlertDialogTheme)
                    .setTitle(R.string.delete_run_title)
                    .setMessage(message)
                    .setPositiveButton(R.string.delete_button, (dialog, which) -> {
                        databaseHelper.deleteRun(runToDelete.getId());
                        loadRuns();
                        Toast.makeText(RunHistoryActivity.this,
                                R.string.run_deleted, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.cancel_button, (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        });
    }

    private void loadRuns() {
        runList = databaseHelper.getAllRuns();
        RunAdapter adapter = new RunAdapter(this, runList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}