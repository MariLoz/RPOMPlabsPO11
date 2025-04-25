package com.example.lab3_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class FragmentUpdate extends Fragment {
    private EditText editTextId, editTextDescription;
    private Button buttonUpdate;
    private DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        editTextId = view.findViewById(R.id.editTextId);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        dbHelper = new DatabaseHelper(getActivity());

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = editTextId.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (!idStr.isEmpty() && !description.isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    dbHelper.updateNote(id, description);
                    editTextId.setText("");
                    editTextDescription.setText("");
                    Toast.makeText(getActivity(), "Запись обновлена", Toast.LENGTH_SHORT).show();


                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("fragment_show");
                    if (fragmentShow != null) {
                        fragmentShow.loadNotes();
                    }
                } else {
                    Toast.makeText(getActivity(), "Введите полный набор данных", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
