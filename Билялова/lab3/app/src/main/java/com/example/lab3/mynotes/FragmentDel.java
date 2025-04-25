package com.example.lab3.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.lab3.R;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private DatabaseHelper dbHelper;
    private EditText editTextId;
    private Button buttonDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        dbHelper = new DatabaseHelper(getActivity());
        editTextId = view.findViewById(R.id.editTextId);
        buttonDelete = view.findViewById(R.id.buttonDelete);

        buttonDelete.setOnClickListener(v -> {
            String idText = editTextId.getText().toString().trim();
            if (!idText.isEmpty()) {
                int id = Integer.parseInt(idText);
                boolean deleted = dbHelper.deleteNote(id);
                if (deleted) {
                    Toast.makeText(getActivity(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                    editTextId.setText("");
                } else {
                    Toast.makeText(getActivity(), "Заметка не найдена", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Введите номер заметки", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

