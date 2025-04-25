package com.example.lab2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lab2.Item;
import com.example.lab2.R;

import java.util.List;

public class CartAdapter  extends ArrayAdapter<Item> {

    private LayoutInflater inflater;
    private int layout;
    private List<Item> goods;

    public List<Item> getGoods()
    {
        return goods;
    }

    public CartAdapter(@NonNull Context context, int resource, List<Item> goods) {
        super(context, resource, goods);
        this.goods = goods;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CartAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new CartAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CartAdapter.ViewHolder) convertView.getTag();
        }

        Item good = goods.get(position);

        viewHolder.product_photo.setImageResource(good.getPhoto());
        viewHolder.product_amount.setText(((Integer)good.getAmount()).toString());
        viewHolder.product_price.setText("$"+((Integer)(good.getPrice()*good.getAmount())).toString());
        //viewHolder.product_id.setText(((Integer)good.getId()).toString());
        viewHolder.product_name.setText(good.getProduct_name());

        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goods.get(position).increment();
                notifyDataSetChanged();
            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goods.get(position).decrement();
                notifyDataSetChanged();
            }
        });

        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goods.get(position).setAmount(0);
                goods.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private class ViewHolder{
        final ImageView product_photo;
        TextView product_name, product_price, product_amount;
        final Button plus, minus, cancel;

        ViewHolder(View view)
        {
            plus = view.findViewById(R.id.plus);
            minus = view.findViewById(R.id.minus);
            cancel = view.findViewById(R.id.cancel);
            product_photo= view.findViewById(R.id.good_photo);
            product_name = view.findViewById(R.id.item_name);
            product_price = view.findViewById(R.id.total_price_current);
            product_amount= view.findViewById(R.id.current_amount);
        }
    }
}
