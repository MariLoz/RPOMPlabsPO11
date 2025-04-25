package by.rpomp.lab_3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import by.rpomp.lab_3.R;
import by.rpomp.lab_3.db.Database;

public class FragmentAdd extends Fragment {
    private Database database;
    private EditText noteDescription;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteDescription = view.findViewById(R.id.noteDescription);
        saveButton = view.findViewById(R.id.saveButton);
        database = new Database(getContext());

        saveButton.setOnClickListener(v -> onClickSaveButton());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.closeConnection();
    }

    private void onClickSaveButton() {
        String description = noteDescription.getText().toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }
        database.openConnection();
        database.addValue(description);
        database.closeConnection();
        noteDescription.setText("");
    }
}
