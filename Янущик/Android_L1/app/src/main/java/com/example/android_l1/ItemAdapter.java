package com.example.android_l1;

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
    private List<Item> items;
    private OnItemClickListener listener;
    private boolean showName, showDescription, showImage;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> items, OnItemClickListener listener, boolean showName, boolean showDescription, boolean showImage) {
        this.items = items;
        this.listener = listener;
        this.showName = showName;
        this.showDescription = showDescription;
        this.showImage = showImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        // Отображаем название, если выбрано в настройках
        if (showName) {
            holder.nameTextView.setText(item.getName());
            holder.nameTextView.setVisibility(View.VISIBLE);
        } else {
            holder.nameTextView.setVisibility(View.GONE);
        }

        // Отображаем описание, если выбрано в настройках
        if (showDescription) {
            holder.descriptionTextView.setText(item.getDescription());
            holder.descriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionTextView.setVisibility(View.GONE);
        }

        // Отображаем изображение, если выбрано в настройках
        if (showImage) {
            Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.imageView);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }

        // Обработчик клика по элементу
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView descriptionTextView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            descriptionTextView = itemView.findViewById(R.id.item_description);
            imageView = itemView.findViewById(R.id.item_image);
        }
    }
}
