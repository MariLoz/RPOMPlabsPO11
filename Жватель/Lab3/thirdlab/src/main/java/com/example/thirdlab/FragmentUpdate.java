package com.example.thirdlab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    private DB db;
    private EditText etNoteNumber, etDescription;
    private Button btnUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        db = new DB(getContext());
        db.open();
        etNoteNumber = view.findViewById(R.id.etNoteNumber);
        etDescription = view.findViewById(R.id.etDescription);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(v -> {
            int noteNumber = Integer.parseInt(etNoteNumber.getText().toString());
            String description = etDescription.getText().toString();
            db.updateNote(noteNumber, description);
            etNoteNumber.setText("");
            etDescription.setText("");
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}