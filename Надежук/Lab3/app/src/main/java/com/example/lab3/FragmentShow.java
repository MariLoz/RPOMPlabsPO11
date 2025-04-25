package com.example.lab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;

public class FragmentShow extends Fragment {
    private ListView notesList;
    private NoteAdapter notesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        notesList = view.findViewById(R.id.notes_list);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        DatabaseAdapter adapter = new DatabaseAdapter(requireContext());
        adapter.open();
        List<Note> notes = adapter.getNotes();
        notesAdapter = new NoteAdapter(getContext(), notes);
        notesList.setAdapter(notesAdapter);
        adapter.close();
    }
}