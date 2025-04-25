package com.example.lab3.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.lab3.Note;
import com.example.lab3.R;
import com.example.lab3.adapters.DatabaseAdapter;
import com.example.lab3.adapters.NotesAdapter;

import java.util.ArrayList;
import java.util.List;

public class AddFragment extends Fragment {

    private DatabaseAdapter dbAdapter;
    private NotesAdapter notesAdapter;

    public AddFragment() {
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
        return inflater.inflate(R.layout.fragment_add, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbAdapter = new DatabaseAdapter(getActivity());

        Button add = view.findViewById(R.id.add_note_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText title = view.findViewById(R.id.add_note_title);
                EditText name = view.findViewById(R.id.add_note_author);
                EditText task = view.findViewById(R.id.add_note_task);
                EditText sub1 = view.findViewById(R.id.add_note_sub1);
                EditText sub2 = view.findViewById(R.id.add_note_sub2);
                EditText sub3 = view.findViewById(R.id.add_note_sub3);

                if(!title.getText().toString().equals("")&&!name.getText().toString().equals("")&&!task.getText().toString().equals(""))
                {
                    ArrayList<String> subs= new ArrayList<String>();

                    if(!sub1.getText().toString().equals(""))
                    {
                        subs.add(sub1.getText().toString());
                    }
                    if(!sub2.getText().toString().equals(""))
                    {
                        subs.add(sub2.getText().toString());
                    }
                    if(!sub3.getText().toString().equals(""))
                    {
                        subs.add(sub3.getText().toString());
                    }
                    insertFullNote(new Note(-1, name.getText().toString(), title.getText().toString(), task.getText().toString()), subs);
                }

            }
        });

    }

    public void insertFullNote(Note note, List<String> subtasks)
    {
        dbAdapter.open();
        long id = dbAdapter.insert(note);
        Log.println(Log.ERROR, "insert id", ((Long)id).toString());
        for (int i =0; i < subtasks.size(); i++)
        {
            dbAdapter.insertSubtask(id, subtasks.get(i));
        }
        dbAdapter.close();
    }


}