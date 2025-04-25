package com.example.rpomp_l3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {

    private NotesDbHelper dbHelper;
    private EditText editTextId;
    private Button buttonDel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        editTextId = view.findViewById(R.id.editTextId);
        buttonDel = view.findViewById(R.id.buttonDel);
        dbHelper = new NotesDbHelper(getContext());

        buttonDel.setOnClickListener(v -> {
            long id = Long.parseLong(editTextId.getText().toString());
            dbHelper.deleteNote(id);
            editTextId.setText("");
        });

        return view;
    }
}