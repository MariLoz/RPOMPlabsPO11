package com.example.laba2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class CartActivity extends AppCompatActivity {
    private ListView listView;
    private GoodsAdapter adapter;
    private ArrayList<Good> selectedGoods;
    private LayoutInflater layoutInflater;
    private View header_view, footer_view;
    private TextView goodsCount, goodsPrice;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedGoods = getIntent().getParcelableArrayListExtra("MyList");

        Collections.sort(selectedGoods);

        listView = (ListView) findViewById(R.id.goodsList);
        searchView = (SearchView) findViewById(R.id.searchView);
        createMyListView(selectedGoods);
        setupSearchView();

        updateFooter();
    }

    private void createMyListView(ArrayList<Good> selectedGoods) {
        adapter = new GoodsAdapter(this, selectedGoods, null);

        layoutInflater = LayoutInflater.from(this);

        header_view = layoutInflater.inflate(R.layout.header_cart, null);
        footer_view = layoutInflater.inflate(R.layout.footer_cart, null);

        goodsCount = (TextView) footer_view.findViewById(R.id.goodsCount);
        goodsPrice = (TextView) footer_view.findViewById(R.id.goodsPrice);

        listView.addHeaderView(header_view);
        listView.addFooterView(footer_view);

        listView.setAdapter(adapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void updateFooter() {
        int totalCount = selectedGoods.size();
        double totalPrice = calculateTotalPrice(selectedGoods);

        goodsCount.setText("Итоговое число товаров: " + totalCount);
        goodsPrice.setText("Итоговая сумма: " + totalPrice + " руб.");
    }

    private double calculateTotalPrice(ArrayList<Good> goods){
        double totalPrice = 0;

        for(Good good : goods){
            totalPrice += good.getPrice();
        }

        return totalPrice;
    }

}