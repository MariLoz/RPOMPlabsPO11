package com.example.lab2k;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private OnCheckedChangeListener listener;

    public ProductAdapter(Context context, List<Product> productList, OnCheckedChangeListener listener) {
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        }

        Product product = productList.get(position);

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        textViewName.setText(product.getName());
        textViewPrice.setText(String.format("$%.2f", product.getPrice()));
        checkBox.setChecked(product.isChecked());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            product.setChecked(isChecked);
            listener.onCheckedChange();
        });

        return convertView;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange();
    }
}
