package com.example.minishop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.minishop.R;
import com.example.minishop.models.Good;
import java.util.ArrayList;

public class checkedGoodsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Good> checkedGoodsList;
    private LayoutInflater layoutInflater;

    public checkedGoodsAdapter(Context context, ArrayList<Good> checkedGoodsList) {
        this.context = context;
        this.checkedGoodsList = checkedGoodsList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return checkedGoodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return checkedGoodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_good, parent, false);
        }

        Good good = checkedGoodsList.get(position);
        TextView tvGoodId = view.findViewById(R.id.tv_goodId);
        TextView tvGoodName = view.findViewById(R.id.tv_goodName);
        TextView tvGoodCost = view.findViewById(R.id.tv_goodCost); // Добавлен TextView для стоимости
        CheckBox cbGood = view.findViewById(R.id.cb_good);

        tvGoodId.setText(String.valueOf(good.getId()));
        tvGoodName.setText(good.getName());
        tvGoodCost.setText(String.format("$%.2f", good.getCost())); // Отображение стоимости
        cbGood.setVisibility(View.GONE); // Скрываем чекбокс в корзине

        return view;
    }
}