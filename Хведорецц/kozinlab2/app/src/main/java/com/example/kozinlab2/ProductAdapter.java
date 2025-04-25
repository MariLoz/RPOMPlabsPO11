package com.example.kozinlab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private boolean isCart;
    private int totalQuantity = 0;
    private TextView totalQuantityTextView;

    public ProductAdapter(Context context, List<Product> productList, boolean isCart) {
        this.context = context;
        this.productList = productList;
        this.isCart = isCart;
        updateTotalQuantity(0); // Обновляем общее количество товаров при создании адаптера
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_product, parent, false);
        }
        TextView textViewProductId = convertView.findViewById(R.id.textViewProductId);
        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView priceTextView = convertView.findViewById(R.id.textViewPrice);
        TextView quantityTextView = convertView.findViewById(R.id.textViewQuantity);
        ImageButton buttonMinus = convertView.findViewById(R.id.buttonMinus);
        ImageButton buttonPlus = convertView.findViewById(R.id.buttonPlus);
        ImageView imageView = convertView.findViewById(R.id.imageViewProduct);
        ImageButton buttonDelete = convertView.findViewById(R.id.buttonDelete);

        Product product = productList.get(position);
        textViewProductId.setText("ID: " + product.getId());
        nameTextView.setText(product.getName());
        priceTextView.setText(String.format("$%.2f", product.getPrice()));
        quantityTextView.setText(String.valueOf(product.getQuantity()));

        imageView.setImageResource(product.getImageResId());

        buttonMinus.setOnClickListener(v -> {
            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - 1);
                totalQuantity--;
                quantityTextView.setText(String.valueOf(product.getQuantity()));
                updateTotalQuantity(0);
            }
        });

        buttonPlus.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            totalQuantity++;
            quantityTextView.setText(String.valueOf(product.getQuantity()));
            updateTotalQuantity(0);
        });

        buttonDelete.setOnClickListener(v -> {
            int quantityToRemove = product.getQuantity();
            productList.remove(position);
            notifyDataSetChanged();  // Обновляем список
            updateTotalQuantity(quantityToRemove);
            // Обновляем общее количество
        });
        if (isCart) {
            buttonDelete.setVisibility(View.VISIBLE);
            buttonDelete.setOnClickListener(v -> {
                productList.remove(position);
                notifyDataSetChanged();
            });
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void updateTotalQuantity(int quantityToRemove) {
        totalQuantity = 0;
        for (Product product : productList) {
            totalQuantity += product.getQuantity();
        }

        totalQuantity -= quantityToRemove;

        // Передаем обновленное количество в активность
        if (context instanceof MainActivity) {
            ((MainActivity) context).updateTotalQuantity(totalQuantity);
        }
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}


