package by.rpomp.lab_3.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import by.rpomp.lab_3.R;
import by.rpomp.lab_3.adapters.NoteListAdapter;
import by.rpomp.lab_3.db.Database;
import by.rpomp.lab_3.entity.Note;
import java.util.ArrayList;
import java.util.List;

public class FragmentShow extends Fragment {
    private List<Note> notes;
    private NoteListAdapter noteListAdapter;
    private Database database;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView notesListView = view.findViewById(R.id.notesListView);

        notes = new ArrayList<>();
        database = new Database(getContext());

        noteListAdapter = new NoteListAdapter(getContext(), notes);
        notesListView.setAdapter(noteListAdapter);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        database.closeConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        notes.clear();
        database.openConnection();
        Cursor cursor = database.getAllData();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
                    notes.add(new Note(id, description));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.closeConnection();
        noteListAdapter.notifyDataSetChanged();

    }
}
