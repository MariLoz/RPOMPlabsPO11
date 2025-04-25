package com.example.lab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private EditText editTextNoteId, editTextNewTitle, editTextNewDescription;
    private NotesDatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        editTextNoteId = view.findViewById(R.id.editTextNoteId);
        editTextNewTitle = view.findViewById(R.id.editTextNewTitle);
        editTextNewDescription = view.findViewById(R.id.editTextNewDescription);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);

        databaseHelper = new NotesDatabaseHelper(getContext());

        buttonUpdate.setOnClickListener(v -> {
            String noteIdStr = editTextNoteId.getText().toString().trim();
            String newTitle = editTextNewTitle.getText().toString().trim();
            String newDescription = editTextNewDescription.getText().toString().trim();

            Toast.makeText(getContext(), "Нажал Сымоник И.А.", Toast.LENGTH_SHORT).show();

            if (noteIdStr.isEmpty() || newTitle.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            } else {
                int noteId = Integer.parseInt(noteIdStr);
                boolean updated = databaseHelper.updateNote(noteId, newTitle, newDescription);

                if (updated) {
                    Toast.makeText(getContext(), "Заметка обновлена!", Toast.LENGTH_SHORT).show();
                    editTextNoteId.setText("");
                    editTextNewTitle.setText("");
                    editTextNewDescription.setText("");
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
