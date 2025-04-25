package com.example.lab1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TaskInfoFragment extends Fragment {

    public static TaskInfoFragment newInstance() {
        return new TaskInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_info, container, false);

        TextView taskDescription = view.findViewById(R.id.taskDescription);
        Button authorButton = view.findViewById(R.id.authorButton);

        // Устанавливаем текст описания задачи
        taskDescription.setText("Разработать приложение для отображения списка элементов с возможностью загрузки данных с сервера, прокрутки списка и отображения детальной информации о каждом элементе.");

        // Обработчик нажатия на кнопку с подписью автора
        authorButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Кнопка нажата Миланой Филиппович Сергеевной", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
