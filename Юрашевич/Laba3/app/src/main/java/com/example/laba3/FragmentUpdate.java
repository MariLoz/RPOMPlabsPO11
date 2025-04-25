package com.example.laba3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private EditText editTextId, editTextDescription;
    private Button buttonUpdate;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        editTextId = view.findViewById(R.id.editTextId);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        dbHelper = new DatabaseHelper(getContext());

        buttonUpdate.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            if (!id.isEmpty() && !description.isEmpty()) {
                try {
                    int noteId = Integer.parseInt(id);
                    if (description.length() > 100) {
                        Toast.makeText(getContext(), "Описание должно быть не более 100 символов", Toast.LENGTH_SHORT).show();
                    } else if (description.matches(".*[<>\"'&].*")) {
                        Toast.makeText(getContext(), "Описание содержит недопустимые символы", Toast.LENGTH_SHORT).show();
                    } else {
                        dbHelper.updateNote(noteId, description);
                        editTextId.setText("");
                        editTextDescription.setText("");
                        Toast.makeText(getContext(), "Запись обновлена", Toast.LENGTH_SHORT).show();

                        FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("f0");
                        if (fragmentShow != null) {
                            fragmentShow.refreshNotes();
                        }
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "ID должен быть числом", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Пожалуйства введите ID и описание", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}