package com.example.mynotes.Fragment;

import android.content.Intent;
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

import com.example.mynotes.DB.DB;
import com.example.mynotes.R;
import com.example.mynotes.TaskInfoActivity;

public class FragmentUpdate extends Fragment {
    private EditText etId, etDescription;
    private Button btnUpdate;
    private DB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update, container, false);

        etId = view.findViewById(R.id.etId);
        etDescription = view.findViewById(R.id.etDescription);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        db = new DB(requireContext());
        db.open();

        btnUpdate.setOnClickListener(v -> {
            String idStr = etId.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (idStr.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            long id;
            try {
                id = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid ID format", Toast.LENGTH_SHORT).show();
                return;
            }

            db.updateNote(id, description);
            etId.setText("");
            etDescription.setText("");

            Toast.makeText(requireContext(), "Note updated", Toast.LENGTH_SHORT).show();

            FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("f0");
            if (fragmentShow != null) {
                fragmentShow.refreshList();
            }
        });

        Button taskInfoButton = view.findViewById(R.id.taskInfoButton);
        Button authorButton = view.findViewById(R.id.authorButton);

        taskInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), TaskInfoActivity.class);
            startActivity(intent);
        });
        authorButton.setOnClickListener(v -> Toast.makeText(requireContext(), "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}