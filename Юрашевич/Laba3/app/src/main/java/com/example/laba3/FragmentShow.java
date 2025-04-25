package com.example.laba3;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

public class FragmentShow extends Fragment {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private CustomAdapter adapter;
    private Button buttonRefresh, buttonReset;
    private Spinner spinnerSort;
    private String currentSortOrder = DatabaseHelper.COLUMN_ID + " ASC";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        listView = view.findViewById(R.id.listView);
        buttonRefresh = view.findViewById(R.id.buttonRefresh);
        buttonReset = view.findViewById(R.id.buttonReset);
        dbHelper = new DatabaseHelper(getContext());

        loadNotes();

        buttonRefresh.setOnClickListener(v -> refreshNotes());
        buttonReset.setOnClickListener(v -> resetDatabase());

        spinnerSort = view.findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentSortOrder = DatabaseHelper.COLUMN_ID + " ASC";
                        break;
                    case 1:
                        currentSortOrder = DatabaseHelper.COLUMN_DESCRIPTION + " ASC";
                        break;
                    case 2:
                        currentSortOrder = DatabaseHelper.COLUMN_CREATED_AT + " DESC";
                        break;
                }
                refreshNotes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshNotes();
    }

    private void loadNotes() {
        Cursor cursor = dbHelper.getAllNotes(currentSortOrder);
        adapter = new CustomAdapter(getContext(), cursor);
        listView.setAdapter(adapter);
    }

    public void refreshNotes() {
        Cursor cursor = dbHelper.getAllNotes(currentSortOrder);
        adapter.swapCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private void resetDatabase() {
        Context context = getContext();
        if (context != null) {
            context.deleteDatabase(DatabaseHelper.DATABASE_NAME);

            dbHelper = new DatabaseHelper(context);
            dbHelper.getWritableDatabase();

            refreshNotes();
        }
    }
}