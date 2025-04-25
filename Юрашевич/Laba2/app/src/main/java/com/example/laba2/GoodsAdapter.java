package com.example.laba2;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GoodsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, Filterable {
    private Context context;
    private ArrayList<Good> checkedGoods = new ArrayList<>();
    private ArrayList<Good> goods;
    private ArrayList<Good> filteredGoods;
    private LayoutInflater layoutInflater;
    private OnChangeListener onChangeListener;

    public GoodsAdapter(Context context, ArrayList<Good> goods, OnChangeListener onChangeListener) {
        this.context = context;
        this.goods = goods;
        this.filteredGoods = new ArrayList<>(goods);
        this.layoutInflater = LayoutInflater.from(context);
        this.onChangeListener = onChangeListener;
    }

    @Override
    public int getCount() {
        return filteredGoods.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredGoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = convertView;
        if (view == null) {
            if (context instanceof CartActivity) {
                view = layoutInflater.inflate(R.layout.list_item_cart, null, false);
            } else {
                view = layoutInflater.inflate(R.layout.list_item, null, false);
            }
        }

        Good goodTemp = filteredGoods.get(position);

        TextView listItemId = view.findViewById(R.id.listItemId);
        listItemId.setText(Integer.toString(goodTemp.getId()));

        ImageView listItemImage = view.findViewById(R.id.listItemImage);
        listItemImage.setImageResource(goodTemp.getGoodImg());
        listItemImage.setBackgroundColor(android.graphics.Color.WHITE);

        TextView listItemName = view.findViewById(R.id.listItemName);
        listItemName.setText(goodTemp.getName());

        TextView listItemPrice = view.findViewById(R.id.listItemPrice);
        listItemPrice.setText("Цена: " + goodTemp.getPrice() + " руб.");

        if (context instanceof MainActivity) {
            CheckBox listItemCheck = view.findViewById(R.id.listItemCheck);
            listItemCheck.setChecked(goodTemp.isCheck());
            listItemCheck.setTag(position);
            listItemCheck.setOnCheckedChangeListener(this);
        }

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.isShown()) {
            int i = (int) compoundButton.getTag();
            goods.get(i).setCheck(isChecked);
            notifyDataSetChanged();

            if (isChecked) {
                checkedGoods.add(goods.get(i));
            } else {
                checkedGoods.remove(goods.get(i));
            }

            onChangeListener.onDataChanged();
        }
    }

    public ArrayList<Good> getCheckedGoods() {
        return checkedGoods;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Good> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(goods);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Good good : goods) {
                        if (good.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(good);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredGoods.clear();
                filteredGoods.addAll((ArrayList<Good>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}