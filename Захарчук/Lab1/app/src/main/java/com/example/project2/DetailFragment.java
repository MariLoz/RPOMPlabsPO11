package com.example.project2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        try {
            view = inflater.inflate(R.layout.fragment_detail, container, false);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка загрузки интерфейса деталей: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }

        Button authorButton = view.findViewById(R.id.authorButton);
        authorButton.setOnClickListener(v -> Toast.makeText(getContext(), "Нажал Захарчук Павел, ПО-11", Toast.LENGTH_SHORT).show());

        try {
            ImageView image = view.findViewById(R.id.detailImage);
            TextView title = view.findViewById(R.id.detailTitle);
            TextView description = view.findViewById(R.id.detailDescription);

            Bundle args = getArguments();
            if (args == null) {
                throw new IllegalArgumentException("Нет данных для отображения");
            }

            String itemTitle = args.getString("title");
            String itemDescription = args.getString("description");
            String itemImageUrl = args.getString("imageUrl");

            if (itemTitle == null || itemDescription == null) {
                throw new IllegalArgumentException("Отсутствуют данные элемента");
            }

            title.setText(itemTitle);
            description.setText(itemDescription);

            // Загрузка изображения
            if (itemImageUrl != null && !itemImageUrl.isEmpty()) {
                Glide.with(this)
                        .load(itemImageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_close_clear_cancel)
                        .into(image);
            } else {
                image.setImageResource(android.R.drawable.ic_menu_gallery);
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка отображения деталей: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return view;
    }
}