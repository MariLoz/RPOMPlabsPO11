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

public class FragmentDel extends Fragment {
    private EditText editTextNoteId;
    private NotesDatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        editTextNoteId = view.findViewById(R.id.editTextNoteId);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);

        databaseHelper = new NotesDatabaseHelper(getContext());

        buttonDelete.setOnClickListener(v -> {
            String noteIdStr = editTextNoteId.getText().toString().trim();
            Toast.makeText(getContext(), "Нажал Сымоник И.А.", Toast.LENGTH_SHORT).show();

            if (noteIdStr.isEmpty()) {
                Toast.makeText(getContext(), "Введите номер заметки!", Toast.LENGTH_SHORT).show();
            } else {
                int noteId = Integer.parseInt(noteIdStr);
                boolean deleted = databaseHelper.deleteNote(noteId);

                if (deleted) {
                    Toast.makeText(getContext(), "Заметка удалена!", Toast.LENGTH_SHORT).show();
                    editTextNoteId.setText("");
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
