package com.example.laba1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {
    private final List<Cryptocurrency> cryptocurrencies;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Cryptocurrency cryptocurrency);
    }

    public CryptoAdapter(List<Cryptocurrency> cryptocurrencies, OnItemClickListener listener) {
        this.cryptocurrencies = cryptocurrencies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crypto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cryptocurrency crypto = cryptocurrencies.get(position);
        if (crypto != null) {
            holder.name.setText(crypto.getName());
            holder.price.setText("Цена: " + crypto.getPrice());

            Glide.with(holder.itemView.getContext())
                    .load(crypto.getImage())
                    .placeholder(android.R.drawable.ic_dialog_info)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.image);

            holder.itemView.setOnClickListener(v -> listener.onItemClick(crypto));
        }
    }

    @Override
    public int getItemCount() {
        return cryptocurrencies != null ? cryptocurrencies.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.crypto_name);
            price = itemView.findViewById(R.id.crypto_price);
            image = itemView.findViewById(R.id.crypto_image);
        }
    }
}