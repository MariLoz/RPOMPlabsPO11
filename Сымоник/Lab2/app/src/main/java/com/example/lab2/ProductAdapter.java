package com.example.lab2;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private TextView footerTextView;

    public ProductAdapter(Context context, List<Product> productList,TextView footerTextView) {
        this.context = context;
        this.productList = productList;
        this.footerTextView = footerTextView;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.idView = convertView.findViewById(R.id.product_id);
            holder.nameView = convertView.findViewById(R.id.product_name);
            holder.priceView = convertView.findViewById(R.id.product_price);
            holder.quantityView = convertView.findViewById(R.id.product_quantity);
            holder.plusButton = convertView.findViewById(R.id.button_plus);
            holder.minusButton = convertView.findViewById(R.id.button_minus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        holder.idView.setText(String.valueOf(product.getId()));
        holder.nameView.setText(product.getName());
        holder.priceView.setText(String.format("%.2f руб.", product.getPrice()));
        holder.quantityView.setText(String.valueOf(product.getQuantity()));

        holder.plusButton.setOnClickListener(v -> {
            product.increaseQuantity();
            holder.quantityView.setText(String.valueOf(product.getQuantity()));
            ((MainActivity) context).updateFooterText();
        });

        holder.minusButton.setOnClickListener(v -> {
            product.decreaseQuantity();
            holder.quantityView.setText(String.valueOf(product.getQuantity()));
            ((MainActivity) context).updateFooterText();
        });

        return convertView;
    }



    private static class ViewHolder {
        TextView idView;
        TextView nameView;
        TextView priceView;
        TextView quantityView;
        Button plusButton;
        Button minusButton;
    }
}
