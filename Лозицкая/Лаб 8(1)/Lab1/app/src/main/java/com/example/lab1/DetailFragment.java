package com.example.lab1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {
    private TextView nameView, nationView, levelView, typeView, armorView, gunView, speedView, costView;
    private ImageView photoView;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        nameView = view.findViewById(R.id.detailName);
        nationView = view.findViewById(R.id.detailNation);
        levelView = view.findViewById(R.id.detailLevel);
        typeView = view.findViewById(R.id.detailType);
        armorView = view.findViewById(R.id.detailArmor);
        gunView = view.findViewById(R.id.detailGun);
        speedView = view.findViewById(R.id.detailSpeed);
        costView = view.findViewById(R.id.detailCost);
        photoView = view.findViewById(R.id.detailPhoto);

        Bundle bundle = getArguments();
        if (bundle != null) {
            nameView.setText("Модель танка: " + bundle.getString("name"));
            nationView.setText("Нация: " + bundle.getString("nation"));
            levelView.setText("Уровень: " + bundle.getInt("level"));
            typeView.setText("Тип: " + bundle.getString("type"));
            armorView.setText("Броня корпуса: " + bundle.getInt("armor") + " мм");
            gunView.setText("Название пушки: " + bundle.getString("gun"));
            speedView.setText("Максимальная скорость: " + bundle.getInt("speed") + " км/ч");
            costView.setText("Стоимость: " + bundle.getInt("cost") + " монет");
            photoView.setImageResource(bundle.getInt("photoResource"));
        }
        return view;
    }
}
