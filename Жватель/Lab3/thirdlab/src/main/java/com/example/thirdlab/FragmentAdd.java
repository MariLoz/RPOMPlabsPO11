package com.example.thirdlab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private DB db;
    private EditText etDescription;
    private Button btnAdd;

    public static FragmentAdd newInstance(DB db) {
        FragmentAdd fragment = new FragmentAdd();
        fragment.db = db;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        etDescription = view.findViewById(R.id.etDescription); // Убрали etNoteNumber
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            if (db == null) {
                db = new DB(getContext());
                db.open();
            }
            String description = etDescription.getText().toString().trim();
            if (!description.isEmpty()) {
                db.addNote(description);
                etDescription.setText("");

                // Обновляем FragmentShow
                FragmentShow fragmentShow = (FragmentShow) getActivity().getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":0");
                if (fragmentShow != null) {
                    fragmentShow.refreshData();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (db != null) {
            db.close();
        }
    }
}