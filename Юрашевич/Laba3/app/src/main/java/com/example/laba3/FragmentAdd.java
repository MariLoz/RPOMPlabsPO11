package com.example.laba3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private EditText editTextDescription;
    private Button buttonAdd;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        dbHelper = new DatabaseHelper(getContext());

        buttonAdd.setOnClickListener(v -> {
            String description = editTextDescription.getText().toString().trim();
            if (!description.isEmpty()) {
                if (description.length() > 100) {
                    Toast.makeText(getContext(), "Описание должно быть не более 100 символов", Toast.LENGTH_SHORT).show();
                } else if (description.matches(".*[<>\"'&].*")) {
                    Toast.makeText(getContext(), "Описание содержит недопустимые символы", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addNote(description);
                    editTextDescription.setText("");
                    Toast.makeText(getContext(), "Запись добавлена", Toast.LENGTH_SHORT).show();

                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("f0");
                    if (fragmentShow != null) {
                        fragmentShow.refreshNotes();
                    }
                }
            } else {
                Toast.makeText(getContext(), "Пожалуйста введите описание", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}