package com.example.android_l1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {
    private TextView titleTextView;
    private TextView descriptionTextView;
    private ImageView imageView;
    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Инициализация UI элементов
        titleTextView = view.findViewById(R.id.detail_title);
        descriptionTextView = view.findViewById(R.id.detail_description);
        imageView = view.findViewById(R.id.detail_image);
        backButton = view.findViewById(R.id.back_button);

        // Получаем данные из аргументов
        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            String description = args.getString("description");
            String imageUrl = args.getString("image");

            titleTextView.setText(title);
            descriptionTextView.setText(description);

            // Загружаем изображение с помощью Glide
            Glide.with(this).load(imageUrl).into(imageView);
        }

        // Обработчик кнопки "Назад"
        backButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ListFragment())
                .commit());

        return view;
    }
}
