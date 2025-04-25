package com.example.android_l2;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<Product> products;
    ArrayList<Product> cart;
    TextView itemCount;
    SparseBooleanArray checkedItems = new SparseBooleanArray();

    public CustomAdapter(Context context, ArrayList<Product> products, ArrayList<Product> cart, TextView itemCount) {
        this.context = context;
        this.products = products;
        this.cart = cart;
        this.itemCount = itemCount;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.price = convertView.findViewById(R.id.price);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.checkBox = convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);
        holder.name.setText(product.name);
        holder.price.setText("Price: $" + product.price);
        holder.imageView.setImageResource(product.imageResId);

        holder.checkBox.setOnCheckedChangeListener(null); // Отключаем слушатель перед изменением состояния
        holder.checkBox.setChecked(checkedItems.get(position, false));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedItems.put(position, isChecked);
            if (isChecked) {
                if (!cart.contains(product)) cart.add(product);
            } else {
                cart.remove(product);
            }
            if (itemCount != null) {
                itemCount.setText("Count of goods = " + cart.size());
            }

            // Обновляем общую цену в CartActivity
            if (context instanceof CartActivity) {
                ((CartActivity) context).updateTotalPrice();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView price;
        ImageView imageView;
        CheckBox checkBox;
    }
}