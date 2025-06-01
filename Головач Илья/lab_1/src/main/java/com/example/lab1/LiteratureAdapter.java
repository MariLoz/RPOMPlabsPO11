package com.example.lab1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class LiteratureAdapter extends RecyclerView.Adapter<LiteratureAdapter.ViewHolder> {
    private final Context context; // Контекст
    private final int resource;   // Идентификатор макета (R.layout.item_country)
    private final List<Literature> states; // Список данных

    // Конструктор с тремя аргументами
    public LiteratureAdapter(Context context, int resource, List<Literature> states) {
        this.context = context;
        this.resource = resource;
        this.states = states;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Используем переданный resource для создания View
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Получаем данные для текущей позиции
        Literature country = states.get(position);

        // Устанавливаем данные в элементы макета
        holder.name.setText(country.getName());
        holder.info.setText(country.getAuthor());

        // Загружаем изображение с помощью Glide
        Glide.with(context)
                .load(country.getBookUrl())
                .into(holder.book);



        // Обработка клика по элементу
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LiteratureDetailActivity.class);
            intent.putExtra("name", country.getName());
            intent.putExtra("info", country.getInfo());
            intent.putExtra("bookUrl", country.getBookUrl());
//            intent.putExtra("author", country.getAuthor());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return states.size(); // Возвращаем количество элементов в списке
    }

    // ViewHolder для элементов списка
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView info;
        public ImageView book;
        public TextView author;

        public ViewHolder(View itemView) {
            super(itemView);
            // Находим элементы макета
            name = itemView.findViewById(R.id.country_name);
            info = itemView.findViewById(R.id.country_info);
            book = itemView.findViewById(R.id.country_book);
//            author = itemView.findViewById(R.id.country_author)
        }
    }
}