package com.example.multimediaplayer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView taskText = findViewById(R.id.taskText);
        String taskDescription = "Полная формулировка задачи: Задание: \n" +
                "Создать приложение, обеспечивающее выбор файла во внешнем хранилище с возможностью дальнейшей его обработки в зависимости от расширения: \n" +
                "- графический файл отобразить с использованием элемента. ImageView\n" +
                "- аудиофайл воспроизвести с использованием элемента MediaPlayer;\n" +
                "- видеофайл воспроизвести с использованием элемента VideoView.\n" +
                "\n" +
                "2. Загрузить заранее набор медиафайлов (изображения, аудио, видео) для тестирования. \n" +
                "3. Создать новый проект.\n" +
                "4. Добавить необходимые элементы интерфейса для реализации всех функций, перечисленных в задании. Для реализации различных функций можно использовать как дополнительные разметки, так и дополнительные активности. Простейший вариант оформления интерфейса приложения приведен в описании работы.\n" +
                "Выполнил: Захарчук Павел, группа ПО-11, лабораторная работа №14.";
        taskText.setText(taskDescription);
    }
}