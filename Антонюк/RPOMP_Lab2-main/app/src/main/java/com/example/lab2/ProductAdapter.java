package com.example.lab2;

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
    private OnCheckboxClickListener onCheckboxClickListener;

    public interface OnCheckboxClickListener {
        void onCheckboxClick();
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnCheckboxClickListener(OnCheckboxClickListener listener) {
        this.onCheckboxClickListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_product, parent, false);
        }

        TextView textViewId = convertView.findViewById(R.id.textViewId);
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewPrice = convertView.findViewById(R.id.textViewPrice);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        final Product product = productList.get(position);
        textViewId.setText("ID: " + product.getId());
        textViewName.setText(product.getName());
        textViewPrice.setText("Итог:" + product.getPrice() + " BYN");
        checkBox.setChecked(product.isChecked());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setChecked(!product.isChecked());
                if (onCheckboxClickListener != null) {
                    onCheckboxClickListener.onCheckboxClick();
                }
            }
        });

        return convertView;
    }
}
