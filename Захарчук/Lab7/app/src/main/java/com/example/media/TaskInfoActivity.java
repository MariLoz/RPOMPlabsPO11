package com.example.media;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView taskText = findViewById(R.id.taskText);
        String taskDescription = "Полная формулировка задачи: \uF06C\tНаличие СОБСТВЕННЫХ элементов управления во всех активностях (масштаб, перелистывание, возврат,…)\n" +
                "\uF06C\tСправка по приложению, наличие сценария\n" +
                "\uF06C\tИспользование различных функций из библиотеки для определения положения, расстояния…\n" +
                "\uF06C\tПрисутствие возможности сохранения истории в базе данных (возможны различные форматы)\n" +
                "Выполнил: Захарчук Павел, группа ПО-11, лабораторная работа №17.";
        taskText.setText(taskDescription);
    }
}