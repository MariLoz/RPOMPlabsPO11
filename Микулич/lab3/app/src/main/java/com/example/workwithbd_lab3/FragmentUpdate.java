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

public class FragmentUpdate extends Fragment {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private ListView listViewNotesUpdate;
    private EditText editTextUpdateId, editTextUpdateName, editTextUpdateDescription, editTextUpdateImageUrl;
    private Button buttonUpdate, buttonRefreshUpdate;
    private NoteAdapter adapter;
    private List<Note> noteList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        listViewNotesUpdate = view.findViewById(R.id.listViewNotesUpdate);
        editTextUpdateId = view.findViewById(R.id.editTextUpdateId);
        editTextUpdateName = view.findViewById(R.id.editTextUpdateName);
        editTextUpdateDescription = view.findViewById(R.id.editTextUpdateDescription);
        editTextUpdateImageUrl = view.findViewById(R.id.editTextUpdateImageUrl);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        buttonRefreshUpdate = view.findViewById(R.id.buttonRefreshUpdate);

        databaseHelper = new DatabaseHelper(getContext());
        db = databaseHelper.getWritableDatabase();

        loadNotes(); // Загружаем данные при запуске

        // Кнопка "Обновить данные"
        buttonUpdate.setOnClickListener(v -> {
            String idText = editTextUpdateId.getText().toString();
            String newName = editTextUpdateName.getText().toString();
            String newDescription = editTextUpdateDescription.getText().toString();
            String newImageUrl = editTextUpdateImageUrl.getText().toString();

            if (idText.isEmpty() || newName.isEmpty() || newDescription.isEmpty() || newImageUrl.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idText);
            String query = "UPDATE " + DatabaseHelper.TABLE_NOTES +
                    " SET " + DatabaseHelper.COLUMN_NAME + "=?, " +
                    DatabaseHelper.COLUMN_DESCRIPTION + "=?, " +
                    DatabaseHelper.COLUMN_IMAGE_URL + "=? WHERE " +
                    DatabaseHelper.COLUMN_ID + "=?";

            db.execSQL(query, new Object[]{newName, newDescription, newImageUrl, id});

            Toast.makeText(getContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
            editTextUpdateId.setText("");
            editTextUpdateName.setText("");
            editTextUpdateDescription.setText("");
            editTextUpdateImageUrl.setText("");

            loadNotes(); // Обновляем список
        });

        // Кнопка "Обновить список"
        buttonRefreshUpdate.setOnClickListener(v -> loadNotes());

        return view;
    }

    private void loadNotes() {
        noteList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL));

                noteList.add(new Note(id, name, description, imageUrl));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new NoteAdapter(getContext(), noteList);
        listViewNotesUpdate.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
