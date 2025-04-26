package com.example.geolocation;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView taskText = findViewById(R.id.taskText);
        String taskDescription = "Полная формулировка задачи: В работе необходимо выполнить отображение:\n" +
                "Карты с выбранными локациями в течение некоторого периода времени. Рекомендуется выбрать локацию, отображающую точки, в которых пользователь побывал в течение недели.\n" +
                "\n" +
                "Маршрута движения\n" +
                "Посещенных мест в течении некоторого времени (не менее 30 фиксций).\n" +
                "Примечание. Информация приложения требует сбора данных в течении продолжительного времени. По указанной причине разработка, тестирование, сбор данных должны быть выполнены пользователем заблаговременно (задолго до защиты лабораторной работы)\n" +
                "Выполнил: Захарчук Павел, группа ПО-11, лабораторная работа №20.";
        taskText.setText(taskDescription);
    }
}