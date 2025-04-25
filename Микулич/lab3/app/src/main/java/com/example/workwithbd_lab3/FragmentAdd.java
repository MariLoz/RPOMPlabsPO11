package com.example.workwithbd_lab3;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class FragmentAdd extends Fragment {

    private EditText editTextName, editTextDescription, editTextImageUrl;
    private Button buttonAddNote;
    private DatabaseHelper databaseHelper;

    public FragmentAdd() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextImageUrl = view.findViewById(R.id.editTextImageUrl);
        buttonAddNote = view.findViewById(R.id.buttonAddNote);

        databaseHelper = new DatabaseHelper(getContext());

        buttonAddNote.setOnClickListener(v -> addNote());

        return view;
    }

    private void addNote() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(getContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        databaseHelper.insertNote(name, description, imageUrl);
        db.close();

        Toast.makeText(getContext(), "Заметка добавлена!", Toast.LENGTH_SHORT).show();

        // Очищаем поля после добавления
        editTextName.setText("");
        editTextDescription.setText("");
        editTextImageUrl.setText("");


    }



}
