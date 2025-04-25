package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentShowMedia extends Fragment {

    private NotesDatabaseHelper dbHelper;
    private ListView listView;
    private CustomAdapter adapter;
    private List<Note> mediaNotes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        dbHelper = new NotesDatabaseHelper(getContext());
        listView = view.findViewById(R.id.listView);

        List<Note> allNotes = dbHelper.getAllNotes();
        mediaNotes = new ArrayList<>();

        for (Note note : allNotes) {
            if ((note.getImagePath() != null && !note.getImagePath().isEmpty()) ||
                    (note.getAudioPath() != null && !note.getAudioPath().isEmpty())) {
                mediaNotes.add(note);
            }
        }

        if (mediaNotes.isEmpty()) {
            Toast.makeText(getContext(), "Нет медиафайлов", Toast.LENGTH_SHORT).show();
        }

        adapter = new CustomAdapter(getContext(), mediaNotes);
        listView.setAdapter(adapter);

        return view;
    }
}
