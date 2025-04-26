package com.example.minishop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.minishop.R;
import com.example.minishop.interfaces.OnChangeListener;
import com.example.minishop.models.Good;
import java.util.ArrayList;

public class GoodsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Good> goodsList;
    private LayoutInflater layoutInflater;
    private OnChangeListener onChangeListener;

    public GoodsAdapter(Context context, ArrayList<Good> goodsList, OnChangeListener onChangeListener) {
        this.context = context;
        this.goodsList = goodsList;
        this.layoutInflater = LayoutInflater.from(context);
        this.onChangeListener = onChangeListener;
    }

    @Override
    public int getCount() {
        return goodsList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsList.get(position);
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

        // Чередование фона: серый для четных позиций, белый для нечетных
        if (position % 2 == 0) {
            view.setBackgroundColor(0xFFFFFFFF); // Белый
        } else {
            view.setBackgroundColor(0xFFE0E0E0); // Серый
        }

        Good good = goodsList.get(position);
        TextView tvGoodId = view.findViewById(R.id.tv_goodId);
        TextView tvGoodName = view.findViewById(R.id.tv_goodName);
        TextView tvGoodCost = view.findViewById(R.id.tv_goodCost); // Добавлен TextView для стоимости
        CheckBox cbGood = view.findViewById(R.id.cb_good);

        tvGoodId.setText(String.valueOf(good.getId()));
        tvGoodName.setText(good.getName());
        tvGoodCost.setText(String.format("$%.2f", good.getCost())); // Отображение стоимости
        cbGood.setChecked(good.isCheck());
        cbGood.setTag(position);
        cbGood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isShown()) {
                int pos = (int) buttonView.getTag();
                goodsList.get(pos).setCheck(isChecked);
                if (onChangeListener != null) {
                    onChangeListener.onDataChanged();
                }
            }
        });

        return view;
    }
}