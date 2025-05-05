package com.example.lab2.minishop;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lab2.R;

import java.util.ArrayList;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> products;
    private ArrayList<Product> selectedProducts;

    public ProductAdapter(Context context, ArrayList<Product> products, ArrayList<Product> selectedProducts) {
        this.context = context;
        this.products = products;
        this.selectedProducts = selectedProducts;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        }

        Product product = products.get(position);

        TextView productName = convertView.findViewById(R.id.productName);
        TextView productPrice = convertView.findViewById(R.id.productPrice);
        CheckBox productCheckbox = convertView.findViewById(R.id.productCheckbox);
        ImageView productImage = convertView.findViewById(R.id.productImage);

        productName.setText(product.getName());
        productPrice.setText(product.getPrice() + " руб.");
        productImage.setImageResource(product.getImageResId());

        productCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedProducts.contains(product)) {
                    selectedProducts.add(product);
                }
            } else {
                selectedProducts.remove(product);
            }
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateSelectedCount();
            }

        });

        productImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product", product);
            context.startActivity(intent);
        });

        return convertView;
    }
}
