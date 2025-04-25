package com.example.workwithbd_lab3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentDel extends Fragment {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private ListView listViewNotesDel;
    private EditText editTextDeleteId;
    private Button buttonDelete, buttonRefresh;
    private NoteAdapter adapter;
    private List<Note> noteList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        listViewNotesDel = view.findViewById(R.id.listViewNotesDel);
        editTextDeleteId = view.findViewById(R.id.editTextDeleteId);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonRefresh = view.findViewById(R.id.buttonRefresh);

        databaseHelper = new DatabaseHelper(getContext());
        db = databaseHelper.getWritableDatabase();

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(getContext(), noteList);
        listViewNotesDel.setAdapter(adapter);

        loadNotes(); // Загружаем данные при запуске фрагмента

        // Кнопка "Удалить"
        buttonDelete.setOnClickListener(v -> {
            String idText = editTextDeleteId.getText().toString();
            if (idText.isEmpty()) {
                Toast.makeText(getContext(), "Введите ID для удаления", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idText);
            int deletedRows = db.delete(DatabaseHelper.TABLE_NOTES, DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});

            if (deletedRows > 0) {
                Toast.makeText(getContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                editTextDeleteId.setText(""); // Очищаем поле ввода
                loadNotes(); // Обновляем список
            } else {
                Toast.makeText(getContext(), "Заметка с таким ID не найдена", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка "Обновить"
        buttonRefresh.setOnClickListener(v -> loadNotes());

        return view;
    }

    private void loadNotes() {
        noteList.clear(); // Очищаем список перед загрузкой
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL));

                noteList.add(new Note(id, name, description, imageUrl));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged(); // Обновляем адаптер
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
