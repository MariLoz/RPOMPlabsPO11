package com.example.rpomp_l3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class FragmentLabInfo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lab_info, container, false);

        // Находим TextView и устанавливаем текст
        TextView textViewLabInfo = view.findViewById(R.id.textViewLabInfo);
        textViewLabInfo.setText("Лабораторная работа № 10. Фрагменты. ViewPager. Хранение информации в базе данных SQLite\n\n" +
                "Выполнил: Янущик Д.Д. ПО-11\n" +
                "Цель: формирование у студентов знаний и навыков работы с фрагментами, \n" +
                "использования View Pager для перелистывания фрагментов, хранения информации \n" +
                "в базе данных SQLite. \n" +
                "План занятия \n" +
                "1. Изучить теоретические сведения. \n" +
                "2. Выполнить практическое задание по лабораторной работе. \n" +
                "3. Оформить отчет и ответить на контрольные вопросы. \n" +
                "Практическое задание \n" +
                "\n" +
                "Одним из доступных способов заранее подготовьте базу данных. База данных, содержащая менее 20 записей будет считаться отсутсвующей. \n" +
                "\n" +
                "1. Разработать приложение MyNotes, представляющее собой View Pager. \n" +
                "\n" +
                "2. Поместить в View Pager четыре фрагмента: FragmentShow, FragmentAdd, FragmentDel, FragmentUpdate. \n" +
                "\n" +
                "3. В View Pager добавить верхнее меню вкладок (PagerTabStrip) с заголовками Show, Add, Del, Update. \n" +
                "\n" +
                "4. Во фрагменте FragmentShow реализовать кастомизированный список заметок ListView с помощью собственного адаптера. \n" +
                "\n" +
                "5. В каждом пункте списка отобразить следующую информацию о заметке пользователя: номер, описание заметки. \n" +
                "\n" +
                "6. Хранение, а также предоставление информации о заметках адаптеру реализовать с помощью базы данных SQLite. \n" +
                "\n" +
                "7. Во фрагменте FragmentAdd реализовать функционал добавления новой заметки посредством ввода описания заметки в поле EditText и добавления информации в базу данных SQLite по нажатию кнопки Add. \n" +
                "\n" +
                "8. Во фрагменте FragmentDel реализовать функционал удаления новой заметки посредством ввода ее номера в поле EditText и удаления информации из базы данных SQLite по нажатию кнопки Del. \n" +
                "\n" +
                "9. Во фрагменте FragmentUpdate реализовать функционал обновления существующей заметки посредством ввода ее номера в поле EditText, ввода нового описания в поле EditText и обновления информации в базе данных SQLite по нажатию кнопки Update. \n" +
                "\n" +
                "10. База данных, содержащая менее 20 записей будет считаться отсутсвующей. ");

        return view;
    }
}