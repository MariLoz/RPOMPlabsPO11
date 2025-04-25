package com.example.minishop_lab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<Product> cartList;
    private UpdateTotalPriceListener listener;

    public interface UpdateTotalPriceListener {
        void onUpdate(double totalPrice);
    }

    public CartAdapter(Context context, List<Product> cartList, UpdateTotalPriceListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return cartList.size();
    }

    @Override
    public Object getItem(int position) {
        return cartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart_layout, parent, false);
            holder = new ViewHolder();
            holder.productImageView = convertView.findViewById(R.id.productImageView);
            holder.nameTextView = convertView.findViewById(R.id.nameProductTextView);
            holder.priceTextView = convertView.findViewById(R.id.priceProductTextView);
            holder.plusButton = convertView.findViewById(R.id.plusButton);
            holder.minusButton = convertView.findViewById(R.id.minusButton);
            holder.countTextView = convertView.findViewById(R.id.countTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = cartList.get(position);

        // Заполняем данные
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText("$" + product.getPrice());
        holder.countTextView.setText(String.valueOf(product.getQuantity()));

        // Загружаем изображение с помощью Glide
        Glide.with(context).load(product.getImageUrl()).into(holder.productImageView);

        // Обработчик кнопки "+"
        holder.plusButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            notifyDataSetChanged();
            updateTotalPrice();
        });

        // Обработчик кнопки "-"
        holder.minusButton.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
            } else {
                cartList.remove(position);
                Toast.makeText(context, "Товар удален: " + product.getName(), Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
            updateTotalPrice();
        });

        return convertView;
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (Product product : cartList) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        listener.onUpdate(totalPrice); // Обновляем общую цену
    }

    private static class ViewHolder {
        ImageView productImageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView countTextView;
        ImageButton plusButton;
        ImageButton minusButton;
    }
}
