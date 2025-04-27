package com.example.thirdlab;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

public class FragmentShow extends Fragment {
    private DB db;
    private ListView listView;
    private NotesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listView);
        db = new DB(getContext());
        db.open();
        Cursor cursor = db.getAllNotes();
        adapter = new NotesAdapter(getContext(), cursor);
        listView.setAdapter(adapter);
        return view;
    }

    public void refreshData() {
        if (db != null && adapter != null) {
            Cursor newCursor = db.getAllNotes();
            adapter.changeCursor(newCursor); // Обновляем курсор в адаптере
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}