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

public class FragmentDelete extends Fragment {
    private EditText noteId;
    private Database database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteId = view.findViewById(R.id.noteId);
        Button deleteButton = view.findViewById(R.id.deleteButton);
        database = new Database(getContext());

        deleteButton.setOnClickListener(v -> onClickDeleteButton());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.closeConnection();
    }

    private void onClickDeleteButton() {
        String id = noteId.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        database.openConnection();
        database.deleteValue(Integer.parseInt(id));
        database.closeConnection();

        noteId.setText("");
    }
}
