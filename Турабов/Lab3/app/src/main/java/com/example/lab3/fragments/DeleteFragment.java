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

import com.example.lab3.R;
import com.example.lab3.adapters.DatabaseAdapter;
import com.example.lab3.adapters.NotesAdapter;

public class DeleteFragment extends Fragment {

    private DatabaseAdapter dbAdapter;
    private NotesAdapter notesAdapter;


    public DeleteFragment() {
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
        return inflater.inflate(R.layout.fragment_delete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbAdapter = new DatabaseAdapter(getActivity());

        Button del = view.findViewById(R.id.delete_note_button);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idEdit = view.findViewById(R.id.delete_note_id);
                if(!idEdit.getText().toString().equals(""))
                {
                    dbAdapter.open();
                    dbAdapter.delete(Integer.parseInt(idEdit.getText().toString()));
                    dbAdapter.close();
                }
            }
        });


    }
}