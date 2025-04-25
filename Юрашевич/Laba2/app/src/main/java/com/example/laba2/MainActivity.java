package com.example.laba2;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnChangeListener {

    private ArrayList<Good> checkedGoods = new ArrayList<Good>();
    private ArrayList<Good> goods = new ArrayList<>(Arrays.asList(
            new Good(1, "Шапка", 8, R.drawable.hat),
            new Good(2, "Перчатки", 5.6, R.drawable.gloves),
            new Good(3, "Шарф", 7.5, R.drawable.scarf),
            new Good(4, "Штаны", 55, R.drawable.pants),
            new Good(5, "Рубашка", 33, R.drawable.shirt),
            new Good(6, "Кроссовки", 125, R.drawable.sneakers),
            new Good(7, "Ветровка", 35, R.drawable.windbreaker),
            new Good(8, "Шорты", 10, R.drawable.shorts),
            new Good(9, "Туфли (М)", 200, R.drawable.shoes_man),
            new Good(10, "Туфли (Ж)", 210, R.drawable.shoes_woman),
            new Good(11, "Кожаная куртка", 250, R.drawable.jacket),
            new Good(12, "Пальто (Ж)", 190, R.drawable.coat_woman)));
    private ListView listView;
    private GoodsAdapter goodsAdapter;
    private View header_view, footer_view;
    private LayoutInflater layoutInflater;
    private Button btnShow;
    private TextView goodsCount;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = (ListView) findViewById(R.id.goodsList);
        searchView = (SearchView) findViewById(R.id.searchView);
        createMyListView();
        setupSearchView();


    }

    private void createMyListView() {
        goodsAdapter = new GoodsAdapter(this, goods, this);

        layoutInflater = LayoutInflater.from(this);

        header_view = layoutInflater.inflate(R.layout.header_main, null);
        footer_view = layoutInflater.inflate(R.layout.footer_main, null);

        btnShow = (Button) footer_view.findViewById(R.id.btnShow);
        btnShow.setOnClickListener(this);

        goodsCount = (TextView) footer_view.findViewById(R.id.goodsCount);

        listView.addHeaderView(header_view);
        listView.addFooterView(footer_view);

        listView.setAdapter(goodsAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                goodsAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        checkedGoods = goodsAdapter.getCheckedGoods();

        Intent intent = new Intent(this, CartActivity.class);
        intent.putParcelableArrayListExtra("MyList", checkedGoods);
        startActivity(intent);
    }

    @Override
    public void onDataChanged() {
        int size = goodsAdapter.getCheckedGoods().size();
        goodsCount.setText("Итоговое число товаров: " + size);
    }
}