package com.example.kozinlab3;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import java.util.ArrayList;

public class FragmentShow extends Fragment {
    private DBInit dbinit;
    private ArrayList<MyNote> notes;
    private MyNoteAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Слушаем событие "update_notes", чтобы обновлять список автоматически
        getParentFragmentManager().setFragmentResultListener("update_notes", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(String requestKey, Bundle result) {
                loadNotes(); // Автоматически обновляем заметки
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        ListView listView = view.findViewById(R.id.listView);
        notes = new ArrayList<>();
        dbinit = new DBInit(getActivity());

        adapter = new MyNoteAdapter(getActivity(), notes);
        listView.setAdapter(adapter);

        loadNotes(); // Загружаем заметки сразу при создании

        return view;
    }

    public void loadNotes() {
        notes.clear();
        Cursor cursor = dbinit.getAllNotes();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String country = cursor.getString(1);
                notes.add(new MyNote(id, country));
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
