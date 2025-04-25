package com.example.lab2.minishop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.lab2.R;
import java.util.ArrayList;
import android.widget.ImageView;


public class CartAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> products;

    public CartAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        }

        Product product = products.get(position);
        TextView name = convertView.findViewById(R.id.cartItemName);
        TextView price = convertView.findViewById(R.id.cartItemPrice);
        ImageView image = convertView.findViewById(R.id.cartItemImage);

        name.setText(product.getName());
        price.setText(product.getPrice() + " руб.");
        image.setImageResource(product.getImageResId());

        return convertView;
    }

}
