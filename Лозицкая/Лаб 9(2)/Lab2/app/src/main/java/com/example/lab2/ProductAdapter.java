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

import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private final List<Product> productList;
    private final LayoutInflater inflater;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    private final OnQuantityChangeListener quantityChangeListener;

    public ProductAdapter(Context context, List<Product> productList, OnQuantityChangeListener listener) {
        this.productList = productList;
        this.inflater = LayoutInflater.from(context);
        this.quantityChangeListener = listener;
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
        return productList.get(position).getId();
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_product, parent, false);
            holder = new ViewHolder();
            holder.cardView = convertView.findViewById(R.id.cardView);
            holder.idTextView = convertView.findViewById(R.id.idProductTextView);
            holder.imageView = convertView.findViewById(R.id.productImageView);
            holder.nameTextView = convertView.findViewById(R.id.nameProductTextView);
            holder.priceTextView = convertView.findViewById(R.id.priceProductTextView);
            holder.quantityTextView = convertView.findViewById(R.id.quantityProductTextView);
            holder.addButton = convertView.findViewById(R.id.addButton);
            holder.removeButton = convertView.findViewById(R.id.removeButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        holder.idTextView.setText("id: " + product.getId());
        holder.nameTextView.setText(product.getName());
        holder.priceTextView.setText(product.getPrice() + " BYN");
        holder.quantityTextView.setText(product.getQuantity() + " шт.");

        Picasso.get()
                .load(product.getImageUrl())
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.imageView);

        holder.addButton.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            holder.quantityTextView.setText(product.getQuantity() + " шт.");
            notifyQuantityChanged();
        });

        holder.removeButton.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - 1);
                holder.quantityTextView.setText(product.getQuantity() + " шт.");
                notifyQuantityChanged();
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
        CardView cardView;
        ImageView imageView;
        TextView idTextView, nameTextView, priceTextView, quantityTextView;
        ImageButton addButton,removeButton;
    }
}
