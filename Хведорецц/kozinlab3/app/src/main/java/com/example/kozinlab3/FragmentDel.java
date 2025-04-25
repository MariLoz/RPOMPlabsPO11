package com.example.kozinlab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

public class FragmentDel extends Fragment {
    private EditText editTextId;
    private Button buttonDel;
    private DBInit dbinit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_del, container, false);

        editTextId = view.findViewById(R.id.editTextId);
        buttonDel = view.findViewById(R.id.buttonDel);
        dbinit = new DBInit(getActivity());

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idStr = editTextId.getText().toString().trim();
                if (!idStr.isEmpty()) {
                    int id = Integer.parseInt(idStr);
                    boolean isDeleted = dbinit.deleteNote(id);  // Попытка удалить запись
                    if (isDeleted) {
                        editTextId.setText("");
                        Toast.makeText(getActivity(), "Заметка успешно удалена", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Запись с таким ID не найдена", Toast.LENGTH_SHORT).show();
                    }

                    // Обновляем отображаемый список
                    FragmentShow fragmentShow = (FragmentShow) getParentFragmentManager().findFragmentByTag("fragment_show");
                    if (fragmentShow != null) {
                        fragmentShow.loadNotes();  // Перезагружаем данные
                    }
                } else {
                    Toast.makeText(getActivity(), "Пожалуйста, введите ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
