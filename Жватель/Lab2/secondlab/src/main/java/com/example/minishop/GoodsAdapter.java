package com.example.minishop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.example.secondlab.R;
import java.util.ArrayList;

public class GoodsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {
    private Context context;
    private ArrayList<Good> arr_goods_adapter;
    private LayoutInflater layoutInflater;
    private OnChangeListener onChangeListener;
    private ArrayList<Good> arr_checked_goods_adapter = new ArrayList<>();
    private boolean isCartMode = false; // Флаг для определения режима (корзина или список)

    public GoodsAdapter(Context context, ArrayList<Good> arr_goods_adapter, OnChangeListener onChangeListener) {
        this.context = context;
        this.arr_goods_adapter = arr_goods_adapter;
        this.layoutInflater = LayoutInflater.from(context);
        this.onChangeListener = onChangeListener;
    }

    // Конструктор для корзины
    public GoodsAdapter(Context context, ArrayList<Good> arr_goods_adapter, boolean isCartMode) {
        this.context = context;
        this.arr_goods_adapter = arr_goods_adapter;
        this.layoutInflater = LayoutInflater.from(context);
        this.isCartMode = isCartMode;
    }

    @Override
    public int getCount() {
        return arr_goods_adapter.size();
    }

    @Override
    public Object getItem(int position) {
        return arr_goods_adapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            if (isCartMode) {
                view = layoutInflater.inflate(R.layout.item_cart, viewGroup, false);
            } else {
                view = layoutInflater.inflate(R.layout.item_good, viewGroup, false);
            }
        }

        Good good = arr_goods_adapter.get(position);
        TextView tvId, tvName, tvPrice;
        CheckBox cb_good = null;

        if (isCartMode) {
            tvId = view.findViewById(R.id.tv_cartId);
            tvName = view.findViewById(R.id.tv_cartName);
            tvPrice = view.findViewById(R.id.tv_cartPrice);
        } else {
            tvId = view.findViewById(R.id.tv_goodId);
            tvName = view.findViewById(R.id.tv_goodName);
            tvPrice = view.findViewById(R.id.tv_goodPrice);
            cb_good = view.findViewById(R.id.cb_good);
        }

        tvId.setText(String.valueOf(good.getId()));
        tvName.setText(good.getName());
        tvPrice.setText(String.format("$%.2f", good.getPrice()));

        if (!isCartMode && cb_good != null) {
            cb_good.setChecked(good.isCheck());
            cb_good.setTag(position);
            cb_good.setOnCheckedChangeListener(this);
        }

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.isShown() && !isCartMode) {
            int position = (int) compoundButton.getTag();
            Good good = arr_goods_adapter.get(position);
            good.setCheck(isChecked);
            if (isChecked) {
                arr_checked_goods_adapter.add(good);
            } else {
                arr_checked_goods_adapter.remove(good);
            }
            onChangeListener.onDataChanged();
            notifyDataSetChanged();
        }
    }

    public ArrayList<Good> getCheckedGoods() {
        return arr_checked_goods_adapter;
    }
}