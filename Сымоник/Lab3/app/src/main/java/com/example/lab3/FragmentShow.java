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
    private ListView listView;
    private NoteAdapter adapter;
    private NotesDatabaseHelper databaseHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listViewNotes);
        databaseHelper = new NotesDatabaseHelper(getContext());

        List<Note> notes = databaseHelper.getAllNotes();
        adapter = new NoteAdapter(getContext(), notes);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        List<Note> notes = databaseHelper.getAllNotes();
        adapter = new NoteAdapter(getContext(), notes);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void updateList() {
        List<Note> notes = databaseHelper.getAllNotes();
        adapter.notifyDataSetChanged();
    }

}
