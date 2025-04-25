package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

public class FragmentShow extends Fragment {

    private ListView listView;
    private NotesDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listView);
        dbHelper = new NotesDatabaseHelper(requireContext());
        updateList(); // Обновляем список при создании фрагмента
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList(); // Обновляем список каждый раз, когда фрагмент становится видимым
    }

    private void updateList() {
        List<Note> notes = dbHelper.getAllNotes();
        List<String> noteStrings = new ArrayList<>();
        for (Note note : notes) {
            noteStrings.add("Note #" + note.getId() + ": " + note.getDescription());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                noteStrings
        );
        listView.setAdapter(adapter);
    }
}