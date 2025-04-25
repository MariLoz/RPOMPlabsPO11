package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {

    private NotesDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        EditText editTextId = view.findViewById(R.id.editTextId);
        EditText editTextDescription = view.findViewById(R.id.editTextDescription);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);
        dbHelper = new NotesDatabaseHelper(getContext());

        buttonUpdate.setOnClickListener(v -> {
            String idStr = editTextId.getText().toString();
            String description = editTextDescription.getText().toString();
            if (!idStr.isEmpty() && !description.isEmpty()) {
                int id = Integer.parseInt(idStr);
                dbHelper.updateNote(id, description);
                editTextId.setText("");
                editTextDescription.setText("");
            }
        });
        return view;
    }
}