package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private NotesDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        dbHelper = new NotesDatabaseHelper(getContext());

        EditText editTextId = view.findViewById(R.id.editTextUpdateId);
        EditText editTextDesc = view.findViewById(R.id.editTextUpdateDesc);
        Button updateButton = view.findViewById(R.id.updateButton);

        updateButton.setOnClickListener(v -> {
            String idInput = editTextId.getText().toString();
            String newDesc = editTextDesc.getText().toString();
            if (!idInput.isEmpty() && !newDesc.isEmpty()) {
                int id = Integer.parseInt(idInput);
                dbHelper.updateNote(id, newDesc);
                editTextId.setText("");
                editTextDesc.setText("");
                Toast.makeText(getContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Введите номер и описание", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
