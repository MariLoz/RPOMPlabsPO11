package com.example.rpomp_l3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentAdd extends Fragment {
    private NotesDbHelper dbHelper;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private CheckBox checkBoxImportant;
    private Button buttonAdd;
    private OnDataChangeListener dataChangeListener;
    public interface OnDataChangeListener {
        void onDataChanged();
    }
    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        checkBoxImportant = view.findViewById(R.id.checkBoxImportant); // Новый CheckBox
        buttonAdd = view.findViewById(R.id.buttonAdd);
        dbHelper = new NotesDbHelper(getContext());

        buttonAdd.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();
            int isImportant = checkBoxImportant.isChecked() ? 1 : 0; // Получаем статус важности
            dbHelper.addNote(title, description, isImportant);
            editTextTitle.setText("");
            editTextDescription.setText("");
            checkBoxImportant.setChecked(false); // Сбрасываем CheckBox

            if (dataChangeListener != null) {
                dataChangeListener.onDataChanged(); // Уведомляем об изменении данных
            }
        });

        return view;
    }
}