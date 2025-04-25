package com.example.kozinlab1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Создаем view для каждого элемента списка
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        // Привязываем данные из поста к элементам UI
        Post post = posts.get(position);
        holder.titleTextView.setText(post.getTitle());
        holder.bodyTextView.setText(post.getBody());
    }

    @Override
    public int getItemCount() {
        // Возвращаем количество элементов
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        // Элементы UI для отображения данных
        TextView titleTextView;
        TextView bodyTextView;

        public PostViewHolder(View itemView) {
            super(itemView);
            // Инициализация элементов UI
            titleTextView = itemView.findViewById(R.id.title);
            bodyTextView = itemView.findViewById(R.id.body);
        }
    }
}
