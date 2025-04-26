package com.example.gestureapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView taskText = findViewById(R.id.taskText);
        String taskDescription = "Полная формулировка задачи: \uF06C\tРазработка собственного СВЯЗНОГО набора жестов\n" +
                "\uF06C\tПредставление в приложении дополнительной справочной информации (текст о назначении приложения)\n" +
                "\uF06C\tВывод различных информационных сообщений после соответствующих жестов ПОЛЬЗОВАТЕЛЯ. Подготовка сценария управления приложением с помощью жестов пользователя\n" +
                "\uF06C\tВозможность навигации по приложению с помощью набора жестов пользователя \n" +
                "Выполнил: Захарчук Павел, группа ПО-11, лабораторная работа №16.";
        taskText.setText(taskDescription);
    }
}