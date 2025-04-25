package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {

    private NotesDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        EditText editTextId = view.findViewById(R.id.editTextId);
        Button buttonDel = view.findViewById(R.id.buttonDel);
        dbHelper = new NotesDatabaseHelper(getContext());

        buttonDel.setOnClickListener(v -> {
            String idStr = editTextId.getText().toString();
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                dbHelper.deleteNote(id);
                editTextId.setText("");
            }
        });
        return view;
    }
}