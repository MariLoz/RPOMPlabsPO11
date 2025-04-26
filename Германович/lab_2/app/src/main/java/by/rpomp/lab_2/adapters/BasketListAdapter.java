package by.rpomp.lab_2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import by.rpomp.lab_2.R;
import by.rpomp.lab_2.models.Product;

public class BasketListAdapter extends BaseAdapter {
    private Context context;
    private List<Product> basketOfGoods;

    public BasketListAdapter(Context context, List<Product> basketOfGoods) {
        this.context = context;
        this.basketOfGoods = basketOfGoods;
    }

    @Override
    public int getCount() {
        return basketOfGoods.size();
    }

    @Override
    public Object getItem(int position) {
        return basketOfGoods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return basketOfGoods.get(position).getId();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.basket_item, parent, false);
        }

        TextView textViewCartId = convertView.findViewById(R.id.textViewCartId);
        TextView textViewCartName = convertView.findViewById(R.id.textViewCartName);
        TextView textViewCartPrice = convertView.findViewById(R.id.textViewCartPrice);

        Product product = basketOfGoods.get(position);
        textViewCartId.setText("ID: " + product.getId());
        textViewCartName.setText(product.getName());
        textViewCartPrice.setText("Price: $" + product.getPrice());

        return convertView;
    }
}
