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

public class FragmentDelete extends Fragment {
    private EditText idEditText;
    private Button deleteButton;
    private DatabaseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete, container, false);
        idEditText = view.findViewById(R.id.id_edit_text);
        deleteButton = view.findViewById(R.id.delete_button);

        adapter = new DatabaseAdapter(requireContext());

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });

        return view;
    }

    private void deleteNote() {
        String idStr = idEditText.getText().toString();
        if (!idStr.isEmpty()) {
            try {
                long id = Long.parseLong(idStr);

                adapter.open();
                long result = adapter.delete(id);
                adapter.close();

                if (result > 0) {
                    Toast.makeText(getContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }

                idEditText.setText("");
            }
            catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Некорректный ID", Toast.LENGTH_SHORT).show();
            }
        }
    }
}