package com.example.lab3.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.lab3.DatabaseHelper;
import com.example.lab3.DetailsActivity;
import com.example.lab3.Note;
import com.example.lab3.R;
import com.example.lab3.adapters.DatabaseAdapter;
import com.example.lab3.adapters.NotesAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowFragment extends Fragment {

    private DatabaseAdapter dbAdapter;
    private NotesAdapter notesAdapter;
    ListView notesList;
    ArrayList<Note> notes= new ArrayList<Note>();

    public ShowFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notesList = view.findViewById(R.id.notes_list);

        dbAdapter = new DatabaseAdapter(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();

        dbAdapter.open();
        notes = (ArrayList<Note>) getNotes();
        dbAdapter.close();

        notesAdapter = new NotesAdapter(getActivity(), R.layout.note_item, notes);
        notesList.setAdapter(notesAdapter);


        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note selected = (Note)parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(Note.class.getSimpleName(), selected);
                dbAdapter.open();
                ArrayList<String> subs = dbAdapter.getSubtasks(selected.get_id());
                //Log.println(Log.ERROR, "me", subs.get(0));
                dbAdapter.close();
                intent.putExtra("subs", subs);
                startActivity(intent);
            }
        };
        notesList.setOnItemClickListener(itemClickListener);
    }

    public List<Note> getNotes(){
        ArrayList<Note> notes = new ArrayList<Note>();
        Cursor cursor = dbAdapter.getAllEntries();
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            String authorName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR));
            String noteTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE));
            String mainTask = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK));
            notes.add(new Note(id, authorName, noteTitle,mainTask));
        }
        cursor.close();
        return  notes;
    }
}