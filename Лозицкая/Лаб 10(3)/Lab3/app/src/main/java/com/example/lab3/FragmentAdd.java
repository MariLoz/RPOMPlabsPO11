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
    private EditText descriptionEditText;
    private Button addButton;
    private DatabaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        descriptionEditText = view.findViewById(R.id.description_edit_text);
        addButton = view.findViewById(R.id.add_button);

        adapter = new DatabaseAdapter(requireContext());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        return view;
    }

    private void addNote() {
        String description = descriptionEditText.getText().toString();
        if (!description.isEmpty()) {
            try {
                Note note = new Note(0, description);

                adapter.open();
                adapter.insert(note);
                adapter.close();

                Toast.makeText(getContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
                descriptionEditText.setText("");
            }
            catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Заметка не добавлена", Toast.LENGTH_SHORT).show();
            }
        }
    }
}