package com.example.lab3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private EditText idEditText;
    private EditText descriptionEditText;
    private Button updateButton;
    private DatabaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        idEditText = view.findViewById(R.id.id_edit_text);
        descriptionEditText = view.findViewById(R.id.description_edit_text);
        updateButton = view.findViewById(R.id.update_button);

        adapter = new DatabaseAdapter(requireContext());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });

        idEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                loadDescription();
            }
        });

        return view;
    }

    private void loadDescription() {
        String idStr = idEditText.getText().toString();
        if (!idStr.isEmpty()) {
            try {
                long id = Long.parseLong(idStr);

                adapter.open();
                Note note = adapter.getNote(id);
                adapter.close();

                if (note != null) {
                    descriptionEditText.setText(note.getDescription());
                } else {
                    descriptionEditText.setText("");
                    Toast.makeText(getContext(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                descriptionEditText.setText("");
                Toast.makeText(getContext(), "Некорректный ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            descriptionEditText.setText("");
        }
    }

    private void updateNote() {
        String idStr = idEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        if (!idStr.isEmpty() && !description.isEmpty()) {
            try {
                long id = Long.parseLong(idStr);
                Note note = new Note(id, description);

                adapter.open();
                long result = adapter.update(note);
                adapter.close();

                if (result > 0) {
                    Toast.makeText(getContext(), "Заметка обновлена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }

                idEditText.setText("");
                descriptionEditText.setText("");
            }
            catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Некорректный ID", Toast.LENGTH_SHORT).show();
            }
        }
    }
}