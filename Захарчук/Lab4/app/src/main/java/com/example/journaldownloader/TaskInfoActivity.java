package com.example.journaldownloader;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView taskText = findViewById(R.id.taskText);
        String taskDescription = "Полная формулировка задачи: Задание 1. Реализуйте пример подключения к сети.\n" +
                "Задание 2. Реализуйте коды приложений в примерах из источника (запросы, взаимодействие с сервером через сокеты). \n" +
                "Задание 3. Разработайте мобильное приложение согласно заданию 3 источника, позволяющее пользователю асинхронно скачивать файлы журнала Научно-технический вестник (возможно взять другой источник файлов подобной структуры). \n" +
                "Задание 4. Хранение и чтение настроек. При запуске приложения пользователю должно выводиться всплывающее полупрозрачное уведомление (popupWindow), с краткой инструкцией по использованию приложения (можете написать случайный текст), чекбоксом «Больше не показывать» и кнопкой «ОК».\n" +
                "\n" +
                "Бонус.\n" +
                "Использование собственного источника документов.\n " +
                "Выполнил: Захарчук Павел, группа ПО-11, лабораторная работа №13.";
        taskText.setText(taskDescription);
    }
}