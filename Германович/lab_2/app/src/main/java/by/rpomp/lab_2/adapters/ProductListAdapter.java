package by.rpomp.lab_2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import by.rpomp.lab_2.models.Product;
import by.rpomp.lab_2.R;
import by.rpomp.lab_2.interfaces.OnCheckboxClickListener;

public class ProductListAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private OnCheckboxClickListener onCheckboxClickListener;

    public ProductListAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    public void setOnCheckboxClickListener(OnCheckboxClickListener onCheckboxClickListener) {
        this.onCheckboxClickListener = onCheckboxClickListener;
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
            convertView = inflater.inflate(R.layout.product_list_item, parent, false);
        }

        TextView textViewId = convertView.findViewById(R.id.productId);
        TextView textViewName = convertView.findViewById(R.id.productName);
        TextView textViewPrice = convertView.findViewById(R.id.productPrice);
        CheckBox checkBox = convertView.findViewById(R.id.checkBox);

        Product product = productList.get(position);
        textViewId.setText(String.valueOf(productList.get(position).getId()));
        textViewName.setText(productList.get(position).getName());
        textViewPrice.setText(String.valueOf(productList.get(position).getPrice()));
        checkBox.setChecked(product.isChecked());

        checkBox.setOnClickListener(view -> {
            product.setChecked(!product.isChecked());
            if (onCheckboxClickListener != null) {
                onCheckboxClickListener.onCheckboxClick();
            }
        });

        return convertView;
    }
}
