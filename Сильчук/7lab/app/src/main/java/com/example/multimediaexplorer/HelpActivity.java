package com.example.multimediaexplorer;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        TextView helpText = findViewById(R.id.helpText);
        helpText.setText("Это справка по приложению MultimediaExplorer.\n\n" +
                "1. Audio Player - воспроизведение аудиофайлов.\n" +
                "2. Video Player - воспроизведение видеофайлов.\n" +
                "3. Camera - создание фотоснимков.\n" +
                "4. Gallery - просмотр изображений.\n\n" +
                "Лабораторная работа 17.\n Приложение для воспроизведения аудио-" +
                " и видео файлов, создания и отображения фотоснимков.\n\n" +
                "Выполнил: Сильчук Д.А.\n" +
                "Проверил: Козинский А.А.\n" +
                "Задание:\n" +
                "Бонусы (то, что способствует оценке выше 4)\n" +
                "- Наличие СОБСТВЕННЫХ элементов управления во всех активностях (масштаб, перелистывание, возврат,…)\n" +
                "- Справка по приложению, наличие сценария\n" +
                "- Использование различных функций из библиотеки для определения положения, расстояния…\n" +
                "- Присутствие возможности сохранения истории в базе данных (возможны различные форматы)\n"

        );
    }
}