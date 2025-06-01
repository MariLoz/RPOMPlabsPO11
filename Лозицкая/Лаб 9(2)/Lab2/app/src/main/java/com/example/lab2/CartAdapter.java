package com.example.lab2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private final LayoutInflater inflater;
    private final List<Product> cartList;

    public interface OnItemRemoveListener {
        void onItemRemove(int productId);
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    private OnItemRemoveListener itemRemoveListener;
    private OnQuantityChangeListener quantityChangeListener;

    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        itemRemoveListener = listener;
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    public CartAdapter(Context context, List<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
        this.inflater = LayoutInflater.from(context);
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
        return cartList.get(position).getId();
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_cart, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.checkedImageView);
            holder.idTextView = convertView.findViewById(R.id.idCheckedTextView);
            holder.nameTextView = convertView.findViewById(R.id.nameCheckedTextView);
            holder.priceTextView = convertView.findViewById(R.id.priceCheckedTextView);
            holder.quantityTextView = convertView.findViewById(R.id.quantityCartTextView);
            holder.removeButton = convertView.findViewById(R.id.removeCartButton);
            holder.addButton = convertView.findViewById(R.id.addCartButton);
            holder.deleteQuantityButton = convertView.findViewById(R.id.deleteQuantityButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = cartList.get(position);
        holder.idTextView.setText("id: " + product.getId());
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(String.format("%.2f BYN", product.getPrice() * product.getQuantity()));
        holder.quantityTextView.setText(product.getQuantity() + " шт.");

        Picasso.get()
                .load(product.getImageUrl())
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.imageView);

        holder.addButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.quantityTextView.setText(product.getQuantity() + " шт.");
            holder.priceTextView.setText(String.format("%.2f BYN", product.getPrice() * product.getQuantity()));
            notifyQuantityChanged();
        });

        holder.removeButton.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - 1);
                holder.quantityTextView.setText(product.getQuantity() + " шт.");

                holder.priceTextView.setText(String.format("%.2f BYN", product.getPrice() * product.getQuantity()));
                notifyQuantityChanged();
            }
        });

        holder.deleteQuantityButton.setOnClickListener(v -> {
            if (itemRemoveListener != null) {
                itemRemoveListener.onItemRemove(product.getId());
            }
        });

        return convertView;
    }

    private void notifyQuantityChanged() {
        if (quantityChangeListener != null) {
            quantityChangeListener.onQuantityChanged();
        }
    }

    static class ViewHolder {
        ImageView imageView;
        TextView idTextView, nameTextView, priceTextView, quantityTextView;
        ImageButton removeButton, addButton, deleteQuantityButton;
    }
}