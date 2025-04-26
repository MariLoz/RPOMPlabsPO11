package com.example.minishop;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);

        TextView taskText = findViewById(R.id.taskText);
        String taskDescription = "Полная формулировка задачи: 1. Разработать приложение MiniShop, состоящее из двух Activity (см. рисунки 3.3, 3.4 в источнике).\n" +
                "2. В первом Activity создать список ListView с Header и Footer.\n" +
                "3. В Footer разместить текстовое поле (TextView) для ввода количества ак-\n" +
                "тивированных пользователем товаров, кнопку Show Checked Items для перехода в корзину товаров.\n" +
                "4. Реализовать кастомизированный список ListView с помощью собственно-\n" +
                "го адаптера, наследующего класс BaseAdapter.\n" +
                "5. В каждом пункте списка отобразить следующую информацию о товаре:\n" +
                "идентификационный номер, название, стоимость, чекбокс для возможности выбора товара пользователем.\n" +
                "6. В текстовом поле (TextView) Footer списка динамически отображать об-\n" +
                "щее текущее количество активированных товаров.\n" +
                "7. При нажатии кнопки Show Checked Items реализовать переход во второе\n" +
                "Activity с корзиной товаров.\n" +
                "8. Корзину товаров реализовать в виде нового кастомизированного списка с\n" +
                "выбранными товарами.\n" +
                "9. Продемонстрировать работу приложения MiniShop на эмуляторе или ре-\n" +
                "альном устройстве.\n" +
                "Выполнил: Захарчук Павел, группа ПО-11, лабораторная работа №9.";
        taskText.setText(taskDescription);
    }
}