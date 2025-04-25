package com.example.lab3_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class FragmentAdd extends Fragment {
    private EditText editTextDescription;
    private Button buttonAdd;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        dbHelper = new DatabaseHelper(getActivity());

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextDescription.getText().toString().trim();
                if (!description.isEmpty()) {
                    dbHelper.addNote(description);
                    editTextDescription.setText("");
                    Toast.makeText(getActivity(), "Запись успешно добавлена", Toast.LENGTH_SHORT).show();


                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("fragment_show");
                    if (fragmentShow != null) {
                        fragmentShow.loadNotes();
                    }
                } else {
                    Toast.makeText(getActivity(), "Введите имя гонщика", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
