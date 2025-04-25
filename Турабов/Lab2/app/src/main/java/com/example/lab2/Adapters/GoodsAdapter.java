package com.example.lab2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lab2.Item;
import com.example.lab2.R;
import com.example.lab2.interfaces.OnChangeListener;

import java.util.ArrayList;
import java.util.List;

public class GoodsAdapter extends ArrayAdapter<Item> {
    private OnChangeListener onChangeListener;
    private LayoutInflater inflater;
    private int layout;
    private List<Item> arr_goods_adapter;
    private List<Item> arr_checked_goods_adapter;

    public void setArr_goods_adapter(List<Item> goods)
    {
        this.arr_goods_adapter = goods;
        notifyDataSetChanged();
    }

    public List<Item> getArr_checked_goods_adapter()
    {
        return arr_checked_goods_adapter;
    }

    public List<Item> getArr_goods_adapter()
    {
        return arr_checked_goods_adapter;
    }

    public GoodsAdapter(@NonNull Context context, int resource, List<Item> arr_goods_adapter,
                        OnChangeListener onChangeListener) {
        super(context, resource, arr_goods_adapter);
        this.arr_goods_adapter = arr_goods_adapter;
        this.arr_checked_goods_adapter = new ArrayList<Item>();
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.onChangeListener = onChangeListener;
    }

//    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//        if (compoundButton.isShown()) {
//            int i = (int) compoundButton.getTag();
//            arr_goods_adapter.get(i).setCheck(isChecked);
//            notifyDataSetChanged();
//            if(isChecked){
//                arr_checked_goods_adapter.add(arr_goods_adapter.get(i));
//            }else {
//                arr_checked_goods_adapter.remove(arr_goods_adapter.get(i));
//            }
//            onChangeListener.onDataChanged();
//        }
//    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Item good = arr_goods_adapter.get(position);

        viewHolder.product_photo.setImageResource(good.getPhoto());
        viewHolder.product_amount.setText(((Integer)good.getAmount()).toString());
        viewHolder.product_price.setText("$"+((Integer)good.getPrice()).toString());
        viewHolder.product_id.setText(((Integer)good.getId()).toString());
        viewHolder.product_name.setText(good.getProduct_name());

        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arr_goods_adapter.get(position).increment();

                if(arr_goods_adapter.get(position).getAmount()==1)
                    arr_checked_goods_adapter.add(arr_goods_adapter.get(position));

                notifyDataSetChanged();
                onChangeListener.onDataChanged();
            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arr_goods_adapter.get(position).decrement();

                if(arr_goods_adapter.get(position).getAmount()==0)
                    arr_checked_goods_adapter.remove(arr_goods_adapter.get(position));

                notifyDataSetChanged();
                onChangeListener.onDataChanged();
            }
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        onChangeListener.onDataChanged();
        arr_checked_goods_adapter.clear();
        for (int i = 0; i<arr_goods_adapter.size(); i++)
        {
            if (arr_goods_adapter.get(i).getAmount()>0)
            {
                arr_checked_goods_adapter.add(arr_goods_adapter.get(i));
            }
        }
    }

    private class ViewHolder{
        final ImageView product_photo;
        TextView product_name, product_price, product_id, product_amount;
        final Button plus, minus;

        ViewHolder(View view)
        {
            plus = view.findViewById(R.id.add);
            minus = view.findViewById(R.id.sub);
            product_photo= view.findViewById(R.id.product_photo);
            product_name = view.findViewById(R.id.product_name);
            product_price = view.findViewById(R.id.product_price);
            product_id = view.findViewById(R.id.product_id);
            product_amount= view.findViewById(R.id.amount_of_current);
        }
    }

}
