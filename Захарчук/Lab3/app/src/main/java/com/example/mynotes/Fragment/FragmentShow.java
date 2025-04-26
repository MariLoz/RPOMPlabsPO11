package com.example.mynotes.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mynotes.DB.DB;
import com.example.mynotes.NotesAdapter;
import com.example.mynotes.R;
import com.example.mynotes.TaskInfoActivity;

public class FragmentShow extends Fragment {
    private ListView listView;
    private NotesAdapter notesAdapter;
    private DB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        listView = view.findViewById(R.id.listView);
        db = new DB(requireContext());
        db.open();

        Cursor cursor = db.getAllData();
        notesAdapter = new NotesAdapter(requireContext(), cursor);
        listView.setAdapter(notesAdapter);

        Button taskInfoButton = view.findViewById(R.id.taskInfoButton);
        Button authorButton = view.findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(requireContext(), "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        return view;
    }

    public void refreshList() {
        Cursor cursor = db.getAllData();
        notesAdapter.refreshCursor(cursor);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}