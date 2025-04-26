package com.example.project2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    OnItemClickListener listener;
    private int maxLines;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(Context context, List<Item> items, OnItemClickListener listener, int maxLines) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.maxLines = maxLines;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка создания элемента списка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            if (holder == null) {
                throw new IllegalStateException("ViewHolder не инициализирован");
            }
            Item item = items.get(position);
            holder.title.setText(item.getTitle());
            holder.subtitle.setText(item.getDescription());
            holder.subtitle.setMaxLines(maxLines);

            // Загрузка изображения с помощью Glide
            if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(item.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(android.R.drawable.ic_menu_gallery) // Заглушка при загрузке
                        .error(android.R.drawable.ic_menu_close_clear_cancel) // Ошибка загрузки
                        .into(holder.image);
            } else {
                holder.image.setImageResource(android.R.drawable.ic_menu_gallery); // Заглушка по умолчанию
            }

            holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка привязки данных: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        try {
            return items.size();
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка получения размера списка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return 0;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            try {
                title = itemView.findViewById(R.id.itemTitle);
                subtitle = itemView.findViewById(R.id.itemSubtitle);
                image = itemView.findViewById(R.id.itemImage);
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "Ошибка инициализации ViewHolder: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}