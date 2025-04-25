package com.example.rpomp_l3;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

public class FragmentShow extends Fragment implements FragmentAdd.OnDataChangeListener {

    private NotesDbHelper dbHelper;
    private ListView listView;
    private NotesAdapter adapter;
    private EditText editTextSearch;
    private Button buttonSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        listView = view.findViewById(R.id.listView);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        dbHelper = new NotesDbHelper(getContext());

        refreshData(); // Загружаем все заметки при открытии

        buttonSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString();
            Cursor cursor = dbHelper.searchNotes(query); // Выполняем поиск
            adapter = new NotesAdapter(getContext(), cursor);
            listView.setAdapter(adapter);
        });

        return view;
    }

    public void refreshData() {
        Cursor cursor = dbHelper.getAllNotes();
        adapter = new NotesAdapter(getContext(), cursor);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDataChanged() {
        refreshData(); // Обновляем данные при изменении
    }
}
