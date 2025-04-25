package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private NotesDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        dbHelper = new NotesDatabaseHelper(getContext());

        EditText editText = view.findViewById(R.id.editTextDel);
        Button delButton = view.findViewById(R.id.delButton);

        delButton.setOnClickListener(v -> {
            String input = editText.getText().toString();
            if (!input.isEmpty()) {
                int id = Integer.parseInt(input);
                dbHelper.deleteNote(id);
                editText.setText("");
                Toast.makeText(getContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Введите номер заметки", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
