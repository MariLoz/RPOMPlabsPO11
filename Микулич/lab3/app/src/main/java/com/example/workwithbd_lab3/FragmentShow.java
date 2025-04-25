package com.example.workwithbd_lab3;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentShow extends Fragment {
    private ListView listView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private DatabaseHelper databaseHelper;
    private Button buttonRefresh;

    public FragmentShow() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        listView = view.findViewById(R.id.listViewNotes);
        databaseHelper = new DatabaseHelper(getContext());
        noteList = new ArrayList<>();

        loadNotesFromDatabase();
        buttonRefresh = view.findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(v -> {
            loadNotesFromDatabaseRefresh();
            Toast.makeText(getContext(), "Данные обновлены", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadNotesFromDatabase() {
        noteList = databaseHelper.getAllNotes();  // Предположим, что у вас есть метод для получения всех записей
        if (noteList != null && !noteList.isEmpty()) {
            if (getActivity() != null) {
                adapter = new NoteAdapter(getActivity(), noteList);
                listView.setAdapter(adapter);
            } else {
                Toast.makeText(getContext(), "Ошибка: фрагмент не привязан к активности", Toast.LENGTH_SHORT).show();
            }
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "Нет заметок для отображения", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadNotesFromDatabaseRefresh() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(getContext()); // Проверяем и создаем, если null
        }

        if (noteList != null) {
            noteList.clear();
            noteList.addAll(databaseHelper.getAllNotes());
            adapter.notifyDataSetChanged();
        } else {
            loadNotesFromDatabase(); // Если список пуст, загружаем заново
        }
    }
}
