package com.example.laba3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    private EditText editTextId;
    private Button buttonDel;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);
        editTextId = view.findViewById(R.id.editTextId);
        buttonDel = view.findViewById(R.id.buttonDel);
        dbHelper = new DatabaseHelper(getContext());

        buttonDel.setOnClickListener(v -> {
            String id = editTextId.getText().toString().trim();
            if (!id.isEmpty()) {
                try {
                    int noteId = Integer.parseInt(id);
                    dbHelper.deleteNote(noteId);
                    editTextId.setText("");
                    Toast.makeText(getContext(), "Запись удалена", Toast.LENGTH_SHORT).show();

                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("f0");
                    if (fragmentShow != null) {
                        fragmentShow.refreshNotes();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "ID должно быть числом", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Пожалуйста введите ID", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}