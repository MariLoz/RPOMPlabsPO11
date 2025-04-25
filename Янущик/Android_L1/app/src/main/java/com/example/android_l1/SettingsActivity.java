package com.example.android_l1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private EditText urlEditText;
    private Spinner urlSpinner;
    private CheckBox nameCheckBox, descriptionCheckBox, imageCheckBox;
    private EditText rowsEditText;
    private Button saveButton;

    private Button dialogButton;

    private SharedPreferences sharedPreferences;
    private Set<String> urlSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        urlEditText = findViewById(R.id.url_edit_text);
        urlSpinner = findViewById(R.id.url_spinner);
        nameCheckBox = findViewById(R.id.name_checkbox);
        descriptionCheckBox = findViewById(R.id.description_checkbox);
        imageCheckBox = findViewById(R.id.image_checkbox);
        rowsEditText = findViewById(R.id.rows_edit_text);
        saveButton = findViewById(R.id.save_button);
        dialogButton = findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(v -> showDialog());

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Загружаем ранее введенные URL
        urlSet = sharedPreferences.getStringSet("saved_urls", new HashSet<>());
        ArrayList<String> urlList = new ArrayList<>(urlSet);
        urlList.add(0, "Выберите URL"); // Добавляем заголовок

        // Настраиваем Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, urlList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urlSpinner.setAdapter(adapter);

        // Обработчик выбора URL из Spinner
        urlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedUrl = parent.getItemAtPosition(position).toString();
                if (!selectedUrl.equals("Выберите URL")) {
                    // Сохраняем выбранный URL в SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("last_url", selectedUrl);
                    editor.apply();
                    Log.d("SettingsActivity", "Selected URL: " + selectedUrl);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ничего не делаем
            }
        });

        // Загружаем сохраненные настройки
        loadSettings();

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        // Загружаем последний введенный URL
        String lastUrl = sharedPreferences.getString("last_url", "");
        urlEditText.setText(lastUrl);

        // Загружаем настройки отображаемых полей
        nameCheckBox.setChecked(sharedPreferences.getBoolean("show_name", true));
        descriptionCheckBox.setChecked(sharedPreferences.getBoolean("show_description", true));
        imageCheckBox.setChecked(sharedPreferences.getBoolean("show_image", true));

        // Загружаем количество строк
        int rows = sharedPreferences.getInt("rows_count", 10);
        rowsEditText.setText(String.valueOf(rows));
    }

    private void saveSettings() {
        String url;

        // Проверяем, выбран ли URL из списка
        String selectedUrl = urlSpinner.getSelectedItem().toString();
        if (!selectedUrl.equals("Выберите URL")) {
            url = selectedUrl; // Используем URL из списка
        } else {
            // Если URL не выбран из списка, берем его из текстового поля
            url = urlEditText.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Введите URL или выберите из списка", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Сохраняем URL в список
        urlSet.add(url);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("saved_urls", urlSet);
        editor.putString("last_url", url);

        // Сохраняем настройки отображаемых полей
        editor.putBoolean("show_name", nameCheckBox.isChecked());
        editor.putBoolean("show_description", descriptionCheckBox.isChecked());
        editor.putBoolean("show_image", imageCheckBox.isChecked());

        // Сохраняем количество строк
        try {
            int rows = Integer.parseInt(rowsEditText.getText().toString());
            if (rows <= 0) {
                Toast.makeText(this, "Количество строк должно быть больше 0", Toast.LENGTH_SHORT).show();
                return;
            }
            editor.putInt("rows_count", rows);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ошибка: введите корректное число строк", Toast.LENGTH_SHORT).show();
            return;
        }

        editor.apply();

        Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void showDialog() {
        // Создаем диалоговое окно
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Описание лабы"); // Заголовок диалога
        builder.setMessage("Выполнил: Янущик Дмитрий ПО-11\nРеализовать интерфейс приложения для отображения списка элементов. В качестве данных для списка использовать файл в формате json, загруженный с удаленного сервера. Загрузка выполняется в ходе работы по команде пользователя, например, «Загрузить данные» \n" +
                "Приложение в минимальном исполнении должно:\n" +
                "- отображать список элементов внутри фрагмента\n" +
                "- список занимает более одного экрана (прокрутка)\n" +
                "- список можно пролистать\n" +
                "- отдельный элемент списка с пользовательским стилем/дизайном\n" +
                "- выполнять запрос на получение данных с удаленного сервера\n" +
                "- выполнять преобразование json-структуры в коллекцию объектов\n" +
                "- выделение отдельного элемента списка с отображение детальной информации на отдельном экране\n" +
                "- отображать детальную информацию об элементе внутри отдельного фрагмента\n" +
                "\n" +
                "Бонусы (то, что способствует оценке выше 4)\n" +
                "- Возможность настраивать приложение (выбирать или вписывать путь к серверу, выбирать размер выводимой информации – число строк, вывод полей и т.п.)\n" +
                "– Присутствие на экране кнопок управления страницами, строками, видом отображаемой информации)\n" +
                "– Возможность выбирать один из нескольких возможных запросов на получение информации \n" +
                "– Преобразование и сохранение информации запроса (например, в текстовый файл или другой формат CSV, локально или в сеть…)\n" +
                "– Передача результатов запроса (электронная почта, месенджер и т. п.)\n" +
                "– Разработка и использование собственного адаптера\n" +
                "– Включение изображений в список \n" +
                "–Обработка исключений с выводом сообщений.\n"); // Текст, который вы хотите отобразить

        // Кнопка "ОК" для закрытия диалога
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Показываем диалоговое окно
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
