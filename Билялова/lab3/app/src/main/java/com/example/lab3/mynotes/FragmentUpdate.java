package com.example.lab3.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.lab3.R;

public class FragmentUpdate extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText editTextId, editTextNewNote, editTextImagePath;
    private Button buttonUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        editTextId = view.findViewById(R.id.editTextId);
        editTextNewNote = view.findViewById(R.id.editTextNewNote);
        editTextImagePath = view.findViewById(R.id.editTextImagePath); // Новое поле для пути к изображению
        buttonUpdate = view.findViewById(R.id.buttonUpdate);

        buttonUpdate.setOnClickListener(v -> {
            String idText = editTextId.getText().toString().trim();
            String newNoteText = editTextNewNote.getText().toString().trim();
            String imagePath = editTextImagePath.getText().toString().trim();

            if (!idText.isEmpty() && !newNoteText.isEmpty()) {
                int id = Integer.parseInt(idText);
                boolean updated = dbHelper.updateNote(id, newNoteText, imagePath); // Передаём 3 аргумента
                if (updated) {
                    Toast.makeText(getActivity(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
                    editTextId.setText("");
                    editTextNewNote.setText("");
                    editTextImagePath.setText("");
                } else {
                    Toast.makeText(getActivity(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Введите номер, текст и путь к изображению", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
