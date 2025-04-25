package com.example.lab3_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class FragmentDel extends Fragment {
    private EditText editTextId;
    private Button buttonDel;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        editTextId = view.findViewById(R.id.editTextId);
        buttonDel = view.findViewById(R.id.buttonDel);
        dbHelper = new DatabaseHelper(getActivity());

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = editTextId.getText().toString().trim();
                if (!idStr.isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    dbHelper.deleteNote(id);
                    editTextId.setText("");
                    Toast.makeText(getActivity(), "Запись по данному номеру удалена", Toast.LENGTH_SHORT).show();


                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("fragment_show");
                    if (fragmentShow != null) {
                        fragmentShow.loadNotes();
                    }
                } else {
                    Toast.makeText(getActivity(), "Пожалуйста, введите номер", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
