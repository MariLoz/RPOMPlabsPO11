package com.example.lab3.mynotes;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import com.example.lab3.R;

public class FragmentShow extends Fragment {
    private DatabaseHelper dbHelper;
    private ListView listView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(getActivity());
        updateList();
        return view;
    }

    private void updateList() {
        Cursor cursor = dbHelper.getAllNotes();
        NoteAdapter adapter = new NoteAdapter(getActivity(), cursor);
        listView.setAdapter(adapter);
    }
}
