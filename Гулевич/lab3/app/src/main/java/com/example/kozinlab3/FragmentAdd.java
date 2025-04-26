package com.example.kozinlab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private EditText editTextCountry;
    private Button buttonAdd;
    private DBInit dbinit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        editTextCountry = view.findViewById(R.id.editTextDescription);
        buttonAdd = view.findViewById(R.id.buttonAdd);
        dbinit = new DBInit(getActivity());

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country = editTextCountry.getText().toString().trim();
                if (!country.isEmpty()) {
                    dbinit.addNote(country);
                    editTextCountry.setText("");
                    Toast.makeText(getActivity(), "Запись добавлена", Toast.LENGTH_SHORT).show();

                    // Отправляем событие обновления во `FragmentShow`
                    Bundle result = new Bundle();
                    result.putBoolean("updated", true);
                    getParentFragmentManager().setFragmentResult("update_notes", result);
                } else {
                    Toast.makeText(getActivity(), "Пожалуйста, введите название", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
