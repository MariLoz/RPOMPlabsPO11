package com.example.mynotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentInfo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        String infoText = "Лабораторная работа №10\n"
                + "Выполнил: Сильчук Д.А.\n"
                + "Проверил: Козинский А.А.\n"
                + "Тема: Фрагменты. ViewPager. Хранение информации в базе данных SQLite.\n\n"
                + "Задание:\n"
                + "1. Разработать приложение MyNotes, представляющее собой View Pager.\n"
                + "2. Поместить в View Pager четыре фрагмента: FragmentShow, FragmentAdd, FragmentDel, FragmentUpdate.\n"
                + "3. В View Pager добавить верхнее меню вкладок (PagerTabStrip) с заголовками Show, Add, Del, Update.\n"
                + "4. Во фрагменте FragmentShow реализовать кастомизированный список заметок ListView с помощью собственного адаптера.\n"
                + "5. В каждом пункте списка отобразить следующую информацию о заметке пользователя: номер, описание заметки.\n"
                + "6. Хранение, а также предоставление информации о заметках адаптеру реализовать с помощью базы данных SQLite.\n"
                + "7. Во фрагменте FragmentAdd реализовать функционал добавления новой заметки посредством ввода описания заметки в поле EditText и добавления информации в базу данных SQLite по нажатию кнопки Add.\n"
                + "8. Во фрагменте FragmentDel реализовать функционал удаления новой заметки посредством ввода ее номера в поле EditText и удаления информации из базы данных SQLite по нажатию кнопки Del.\n"
                + "9. Во фрагменте FragmentUpdate реализовать функционал обновления существующей заметки посредством ввода ее номера в поле EditText, ввода нового описания в поле EditText и обновления информации в базе данных SQLite по нажатию кнопки Update.\n"
                + "10. База данных, содержащая менее 20 записей будет считаться отсутствующей.";

        // Находим TextView и устанавливаем текст
        TextView textViewInfo = view.findViewById(R.id.textViewInfo);
        textViewInfo.setText(infoText);

        return view;
    }
}