package com.example.minishop_lab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private int selectedCount = 0;
    private UpdateSelectedCountListener listener;

    public interface UpdateSelectedCountListener {
        void onUpdate(int count);
    }

    public ProductAdapter(Context context, List<Product> productList, UpdateSelectedCountListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
            holder = new ViewHolder();
            holder.productImageView = convertView.findViewById(R.id.productImageView);
            holder.nameTextView = convertView.findViewById(R.id.nameProductTextView);
            holder.priceTextView = convertView.findViewById(R.id.priceProductTextView);
            holder.checkBox = convertView.findViewById(R.id.addToBasketCheckBox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);

        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(product.getPrice() + " $");

        // очистка повторных слушателей
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(product.isChecked());

        Glide.with(context).load(product.getImageUrl()).into(holder.productImageView);

        // Устанавливаем слушатель на изменение состояния чекбокса
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setQuantity(isChecked ? 1 : 0); // Если выбрано – 1, если нет – 0
            product.setChecked(isChecked); // Обновляем состояние чекбокса
            updateSelectedCount();
            // Обновляем счетчик выбранных элементов
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView productImageView;
        TextView nameTextView;
        TextView priceTextView;
        CheckBox checkBox;
    }

    private void updateSelectedCount() {
        int count = 0;
        for (Product product : productList) {
            if (product.isChecked()) {
                count++;
            }
        }
        if (listener != null) {
            listener.onUpdate(count); // Обновляем количество в MainActivity
        }
    }
}
