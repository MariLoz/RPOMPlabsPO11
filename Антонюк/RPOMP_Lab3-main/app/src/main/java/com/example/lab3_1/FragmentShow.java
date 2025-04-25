package com.example.lab3_1;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class FragmentShow extends Fragment {
    private DatabaseHelper dbHelper;
    private ArrayList<Note> notes;
    private NoteAdapter adapter;
    private Button buttonRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);


        buttonRefresh = view.findViewById(R.id.buttonRefresh);
        ListView listView = view.findViewById(R.id.listView);
        notes = new ArrayList<>();
        dbHelper = new DatabaseHelper(getActivity());


        adapter = new NoteAdapter(getActivity(), notes);
        listView.setAdapter(adapter);


        loadNotes();


        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNotes();
            }
        });

        return view;
    }


    public void loadNotes() {
        notes.clear();
        Cursor cursor = dbHelper.getAllNotes();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String description = cursor.getString(1);
                notes.add(new Note(id, description));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
