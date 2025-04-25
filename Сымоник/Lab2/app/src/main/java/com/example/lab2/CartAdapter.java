package com.example.lab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;

    public CartAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
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
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.cart_list_item, parent, false);
        }

        Product currentProduct = productList.get(position);

        TextView itemName = convertView.findViewById(R.id.cartItemName);
        TextView itemQuantity = convertView.findViewById(R.id.cartItemQuantity);
        TextView itemPrice = convertView.findViewById(R.id.cartItemPrice);

        itemName.setText(currentProduct.getName());
        itemQuantity.setText("x" + currentProduct.getQuantity());
        itemPrice.setText(currentProduct.getPrice() + " руб.");

        return convertView;
    }
}
