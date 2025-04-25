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

public class FragmentAdd extends Fragment {
    private EditText editTextTitle, editTextDescription;
    private NotesDatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);

        databaseHelper = new NotesDatabaseHelper(getContext());

        buttonAdd.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            Toast.makeText(getContext(), "Нажал Сымоник И.А.", Toast.LENGTH_SHORT).show();

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.addNote(title, description);
                Toast.makeText(getContext(), "Заметка добавлена!", Toast.LENGTH_SHORT).show();
                editTextTitle.setText("");
                editTextDescription.setText("");
            }
        });

        return view;
    }
}
