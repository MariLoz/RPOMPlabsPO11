package com.example.kozinlab1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final List<Item> itemList;
    private final ItemClickListener itemClickListener;

    // Добавляем параметр для обработки кликов
    public ItemAdapter(List<Item> itemList, ItemClickListener itemClickListener) {
        this.itemList = itemList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.descriptionTextView.setText(item.getDescription());

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .override(holder.imageView.getWidth(), holder.imageView.getHeight())
                .into(holder.imageView);

        // Устанавливаем обработчик клика
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(item));
    }
    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);

        // Перезапускаем Glide при перераспределении элементов
        Glide.with(holder.itemView.getContext()).clear(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            imageView = itemView.findViewById(R.id.imageViewItem);
        }
    }


    public interface ItemClickListener {
        void onItemClick(Item item);
    }
}
