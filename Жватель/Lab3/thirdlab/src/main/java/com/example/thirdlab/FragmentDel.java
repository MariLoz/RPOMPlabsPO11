package com.example.thirdlab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private DB db;
    private EditText etNoteNumber;
    private Button btnDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        db = new DB(getContext());
        db.open();
        etNoteNumber = view.findViewById(R.id.etNoteNumber);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            int noteNumber = Integer.parseInt(etNoteNumber.getText().toString());
            db.deleteNote(noteNumber);
            etNoteNumber.setText("");
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}