package com.example.lab1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;

public class PhonesAdapter extends RecyclerView.Adapter<PhonesAdapter.GuitarAmpViewHolder> {

    private List<Phones> guitarAmpList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    // Интерфейс для обработки нажатий
    public interface OnItemClickListener {
        void onItemClick(Phones guitarAmp);
    }

    public PhonesAdapter(Context context, List<Phones> guitarAmpList, OnItemClickListener listener) {
        this.context = context;
        this.guitarAmpList = guitarAmpList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public GuitarAmpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new GuitarAmpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuitarAmpViewHolder holder, int position) {
        Phones amp = guitarAmpList.get(position);
        holder.textTitle.setText(amp.getName());
        holder.textDescription.setText(amp.getDescription());

        // Загрузка изображения с Glide
        Glide.with(context)
                .load(amp.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery) // Временная заглушка
                .error(android.R.drawable.stat_notify_error) // Картинка при ошибке
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Ошибка загрузки изображения: " + (e != null ? e.getMessage() : "Unknown error"));
                        return false; // Сообщить Glide, что он должен продолжать выполнение
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Изображение успешно загружено");
                        return false;
                    }
                })
                .into(holder.imageItem);

        // Обработчик нажатий на элемент списка
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(amp));
    }

    @Override
    public int getItemCount() {
        return guitarAmpList.size();
    }

    public static class GuitarAmpViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;
        TextView textTitle, textDescription;

        public GuitarAmpViewHolder(View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.imageItem);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }
}
