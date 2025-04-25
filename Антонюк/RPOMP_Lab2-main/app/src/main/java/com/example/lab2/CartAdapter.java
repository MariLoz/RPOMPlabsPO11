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
    private List<Product> cartItems;

    public CartAdapter(Context context, List<Product> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return cartItems.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_cart, parent, false);
        }

        TextView textViewCartId = convertView.findViewById(R.id.textViewCartId);
        TextView textViewCartName = convertView.findViewById(R.id.textViewCartName);
        TextView textViewCartPrice = convertView.findViewById(R.id.textViewCartPrice);

        Product product = cartItems.get(position);
        textViewCartId.setText("ID: " + product.getId());
        textViewCartName.setText(product.getName());
        textViewCartPrice.setText("Итог: " + product.getPrice() + " BYN");

        return convertView;
    }
}
