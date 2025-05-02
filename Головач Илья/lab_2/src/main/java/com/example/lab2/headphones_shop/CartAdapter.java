package com.example.lab2.headphones_shop;

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
    private ArrayList<Headphones> headphones;

    public CartAdapter(Context context, ArrayList<Headphones> products) {
        this.context = context;
        this.headphones = products;
    }

    @Override
    public int getCount() {
        return headphones.size();
    }

    @Override
    public Object getItem(int position) {
        return headphones.get(position);
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

        Headphones product = headphones.get(position);
        TextView name = convertView.findViewById(R.id.cartItemName);
        TextView price = convertView.findViewById(R.id.cartItemPrice);
        ImageView image = convertView.findViewById(R.id.cartItemImage);

        name.setText(product.getName());
        price.setText(product.getPrice() + " BYN");
        image.setImageResource(product.getImageResId());

        return convertView;
    }

}
