package com.example.rpomp_l3;

import android.os.Bundle;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {

    private NotesDbHelper dbHelper;
    private EditText editTextId;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private CheckBox checkBoxImportant;
    private Button buttonUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        editTextId = view.findViewById(R.id.editTextId);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        checkBoxImportant = view.findViewById(R.id.checkBoxImportant); // Новый CheckBox
        buttonUpdate = view.findViewById(R.id.buttonUpdate);
        dbHelper = new NotesDbHelper(getContext());

        buttonUpdate.setOnClickListener(v -> {
            long id = Long.parseLong(editTextId.getText().toString());
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();
            int isImportant = checkBoxImportant.isChecked() ? 1 : 0; // Получаем статус важности
            dbHelper.updateNote(id, title, description, isImportant);
            editTextId.setText("");
            editTextTitle.setText("");
            editTextDescription.setText("");
            checkBoxImportant.setChecked(false); // Сбрасываем CheckBox

            // Обновляем данные в FragmentShow
            FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("fragment_show");
            if (fragmentShow != null) {
                fragmentShow.refreshData();
            }
        });

        return view;
    }
}