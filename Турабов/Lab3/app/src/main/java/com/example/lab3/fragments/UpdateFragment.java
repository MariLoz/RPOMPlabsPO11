package com.example.lab3.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.lab3.Note;
import com.example.lab3.R;
import com.example.lab3.adapters.DatabaseAdapter;
import com.example.lab3.adapters.NotesAdapter;

public class UpdateFragment extends Fragment {

    private DatabaseAdapter dbAdapter;
    private NotesAdapter notesAdapter;

    public UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbAdapter = new DatabaseAdapter(getActivity());

        Button update = view.findViewById(R.id.update_note_button);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText id = view.findViewById(R.id.update_note_id);
                EditText name = view.findViewById(R.id.update_note_author);
                EditText title = view.findViewById(R.id.update_note_title);
                EditText task = view.findViewById(R.id.update_note_task);

                dbAdapter.open();
                dbAdapter.update(new Note(Integer.parseInt(id.getText().toString()), name.getText().toString(), title.getText().toString(), task.getText().toString()));
                dbAdapter.close();

            }
        });

    }
}