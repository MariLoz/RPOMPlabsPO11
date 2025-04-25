package com.example.lab1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class PostDetailFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_IMAGE_URL = "imageUrl";  // Изменил на imageUrl

    public static PostDetailFragment newInstance(String title, String imageUrl) {
        PostDetailFragment fragment = new PostDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_IMAGE_URL, imageUrl);  // Передаем imageUrl
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        TextView detailTitle = view.findViewById(R.id.detailTitle);
        ImageView detailImage = view.findViewById(R.id.detailImage);  // Добавляем ImageView для изображения

        if (getArguments() != null) {
            detailTitle.setText(getArguments().getString(ARG_TITLE));
            String imageUrl = getArguments().getString(ARG_IMAGE_URL);

            // Загружаем изображение с помощью Glide
            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)  // Можно поставить плейсхолдер
                    .into(detailImage);  // Устанавливаем изображение в ImageView
        }

        return view;
    }
}
