package com.example.thirdlab;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.widget.Button; // Добавлен импорт для Button
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ViewPager pager;
    private MyFragmentPagerAdapter pagerAdapter;
    private DB db;
    private Button infoButton, pressButton;

    private static final String TASK_DESCRIPTION = "Лабораторная работа №10\n" +
            "Разработать приложение MyNotes, представляющее собой ViewPager.\n" +
            "1. Поместить в ViewPager четыре фрагмента: FragmentShow, FragmentAdd, FragmentDel, FragmentUpdate.\n" +
            "2. Добавить в ViewPager верхнее меню вкладок (PagerTabStrip) с заголовками Show, Add, Del, Update.\n" +
            "3. Во фрагменте FragmentShow реализовать кастомизированный список заметок ListView с помощью собственного адаптера.\n" +
            "4. В каждом пункте списка отобразить следующую информацию о заметке пользователя: номер, описание заметки.\n" +
            "5. Хранение и предоставление информации о заметках адаптеру реализовать с помощью базы данных SQLite.\n" +
            "6. Во фрагменте FragmentAdd реализовать функционал добавления новой заметки посредством ввода описания заметки в поле EditText и добавления информации в базу данных SQLite по нажатию кнопки Add.\n" +
            "7. Во фрагменте FragmentDel реализовать функционал удаления новой заметки посредством ввода ее номера в поле EditText и удаления информации из базы данных SQLite по нажатию кнопки Del.\n" +
            "8. Во фрагменте FragmentUpdate реализовать функционал обновления существующей заметки посредством ввода ее номера в поле EditText, ввода нового описания в поле EditText и обновления информации в базе данных SQLite по нажатию кнопки Update.\n" +
            "9. База данных, содержащая менее 20 записей, будет считаться отсутствующей.\n" +
            "10. Одним из доступных способов заранее подготовить базу данных.";

    private static final String AUTHOR_INFO = "Выполнил: Жватель Станислав Сергеевич\n" +
            "Группа: ПО-11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pager = findViewById(R.id.pager);
        infoButton = findViewById(R.id.infoButton); // Инициализация кнопки
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        db = new DB(this);
        db.open();
        db.writeInitialData(); // Populate with initial data

        Button backButton = findViewById(R.id.pressButton);
        backButton.setOnClickListener(v -> {
            Toast.makeText(this, "Нажал Жватель!", Toast.LENGTH_SHORT).show();
            // Можно добавить finish() если хотите закрыть активность при нажатии
            // finish();
        });

        // Обработчик нажатия на кнопку Info с анонимным классом
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private void showInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Информация о задании");
        builder.setMessage(TASK_DESCRIPTION + "\n\n" + AUTHOR_INFO);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}