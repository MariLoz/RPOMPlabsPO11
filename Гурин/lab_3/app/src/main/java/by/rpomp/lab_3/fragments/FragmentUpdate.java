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

public class FragmentUpdate extends Fragment {
    private EditText noteId, noteDescription;
    private Button updateButton;
    private Database database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteId = view.findViewById(R.id.noteId);
        noteDescription = view.findViewById(R.id.noteDescription);
        updateButton = view.findViewById(R.id.updateButton);
        database = new Database(getContext());

        updateButton.setOnClickListener(v -> onClickUpdateButton());
    }

    private void onClickUpdateButton() {
        String id = noteId.getText().toString().trim();
        String description = noteDescription.getText().toString().trim();

        if (id.isEmpty() || description.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        database.openConnection();
        database.updateValue(Integer.parseInt(id), description);
        database.closeConnection();

        noteId.setText("");
        noteDescription.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.closeConnection();
    }
}