package com.example.minishop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.secondlab.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnChangeListener, View.OnClickListener {

    private ListView listView;
    private ArrayList<Good> arr_goods = new ArrayList<>();
    private GoodsAdapter goodsAdapter;
    private LayoutInflater layoutInflater;
    private View view_header, view_footer;
    private Button btnShow;
    private TextView tv_count;
    private Button infoButton; // Кнопка для отображения информации

    // Информация о задаче и авторе
    private static final String TASK_DESCRIPTION = "Лабораторная работа №9\n" +
            " Разработать приложение MiniShop, состоящее из двух Activity (см. рисунки 3.3, 3.4 в источнике).\n" +
            " В первом Activity создать список ListView с Header и Footer.\n" +
            " В Footer разместить текстовое поле для ввода количества активированных товаров и кнопку Show Checked Items для перехода в корзину.\n" +
            " Реализовать кастомизированный список ListView с помощью собственного адаптера, наследующего класс BaseAdapter.\n" +
            " В каждом пункте списка отобразить идентификационный номер, название, стоимость, чекбокс для выбора товара.\n" +
            " Динамически отображать общее количество активированных товаров в текстовом поле Footer.\n" +
            " При нажатии кнопки Show Checked Items реализовать переход во второе Activity с корзиной товаров.\n" +
            " Корзину реализовать в виде нового кастомизированного списка с выбранными товарами.\n" +
            " Продемонстрировать работу на эмуляторе или реальном устройстве.";

    private static final String AUTHOR_INFO = "Выполнил: Жватель Станислав Сергеевич\n" +
            "Группа: ПО-11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        createMyListView();
    }

    private void initView() {
        listView = findViewById(R.id.listView);
        if (listView == null) {
            throw new IllegalStateException("ListView not found in activity_main.xml");
        }
    }

    private void createMyListView() {
        fillData();
        goodsAdapter = new GoodsAdapter(this, arr_goods, this);
        layoutInflater = LayoutInflater.from(this);
        view_header = layoutInflater.inflate(R.layout.header_mygoods, null);
        view_footer = layoutInflater.inflate(R.layout.footer_mygoods, null);
        btnShow = view_footer.findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);
        tv_count = view_footer.findViewById(R.id.tv_count);
        infoButton = view_footer.findViewById(R.id.infoButton); // Инициализация кнопки в футере
        infoButton.setOnClickListener(this); // Устанавливаем обработчик
        listView.addHeaderView(view_header);
        listView.addFooterView(view_footer);
        listView.setAdapter(goodsAdapter);
    }

    private void fillData() {
        int i = 1;
        if(i < 2) {
            double price = (Math.random() * 98 + 1) + 0.99;
            arr_goods.add(new Good(i, "Жватель Станислав", false, price));
        }
        for (i = 2; i <= 25; i++) {
            double price = (Math.random() * 98 + 1) + 0.99;
            arr_goods.add(new Good(i, "Good №" + i, false, price));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnShow) {
            ArrayList<Good> checkedGoods = goodsAdapter.getCheckedGoods();
            Intent intent = new Intent(this, SecondActivity.class);
            intent.putParcelableArrayListExtra("MyList", checkedGoods);
            startActivity(intent);
        } else if (v.getId() == R.id.infoButton) {
            showTaskInfo();
        }
    }

    @Override
    public void onDataChanged() {
        int size = goodsAdapter.getCheckedGoods().size();
        tv_count.setText("Goods count = " + size);
    }

    private void showTaskInfo() {
        String fullInfo = TASK_DESCRIPTION + "\n\n" + AUTHOR_INFO;
        new AlertDialog.Builder(this)
                .setTitle("Информация о задании")
                .setMessage(fullInfo)
                .setPositiveButton("Закрыть", (dialog, which) -> dialog.dismiss())
                .show();
    }
}