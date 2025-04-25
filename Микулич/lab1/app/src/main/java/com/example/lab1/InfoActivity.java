package com.example.lab1;

import android.text.Html;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private ArrayList<Phones> guitarAmpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Spinner spinnerInfo = findViewById(R.id.spinnerInfo);
        TextView infoText = findViewById(R.id.textInfo);

        infoText.setText(Html.fromHtml(
                "<b>Разработчик:</b>Микулич Максим Игоревич<br>" +
                        "<b>Группа:</b> ПО-11<br>" +
                        "<b>Тема:</b> Отображение списка элементов на Android загруженного с использованием JSON<br>" +
                        "<b>Предмет:</b> Разработка программного обеспечения для мобильных платформ<br>" +
                        "<b>Номер работы:</b> Лабораторная работа №8"
        ));

        // Получаем список объектов из MainActivity
        guitarAmpList = (ArrayList<Phones>) getIntent().getSerializableExtra("ampList");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Справка", "Линейный список", "Сетка"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInfo.setAdapter(adapter);

        spinnerInfo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    navigateToMain(0);
                } else if (position == 2) {
                    navigateToMain(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerInfo.setSelection(0);
    }

    private void navigateToMain(int layoutType) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("layoutType", layoutType);
        intent.putExtra("ampList", guitarAmpList);
        startActivity(intent);
        finish();
    }
}
