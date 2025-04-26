package com.example.kozinlab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class FragmentUpdate extends Fragment {
    private EditText editTextId, editTextCountry;
    private Button buttonUpdate;
    private DBInit dbinit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        editTextId = view.findViewById(R.id.editTextId);
        editTextCountry = view.findViewById(R.id.editTextDescription);
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        dbinit = new DBInit(getActivity());

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = editTextId.getText().toString().trim();
                String country = editTextCountry.getText().toString().trim();

                if (!idStr.isEmpty() && !country.isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    dbinit.updateNote(id, country);
                    editTextId.setText("");
                    editTextCountry.setText("");
                    Toast.makeText(getActivity(), "Запись успешно обновлена", Toast.LENGTH_SHORT).show();


                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("fragment_show");
                    if (fragmentShow != null) {
                        fragmentShow.loadNotes();
                    }
                } else {
                    Toast.makeText(getActivity(), "Пожалуйста введите ID и название", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}