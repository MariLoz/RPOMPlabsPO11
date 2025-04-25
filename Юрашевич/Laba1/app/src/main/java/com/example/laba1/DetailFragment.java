package com.example.laba1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;

public class DetailFragment extends Fragment {
    private static final String TAG = "DetailFragment";
    private Cryptocurrency cryptocurrency;

    public static DetailFragment newInstance(Cryptocurrency cryptocurrency) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("crypto", cryptocurrency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cryptocurrency = (Cryptocurrency) getArguments().getSerializable("crypto");
            Log.d(TAG, "Received crypto: " + (cryptocurrency != null ? cryptocurrency.getName() : "null"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        try {
            if (getArguments() == null || getArguments().getSerializable("crypto") == null) {
                throw new IllegalStateException("Данные криптовалюты не найдены");
            }

            cryptocurrency = (Cryptocurrency) getArguments().getSerializable("crypto");

            TextView name = view.findViewById(R.id.detail_name);
            TextView description = view.findViewById(R.id.detail_description);
            TextView price = view.findViewById(R.id.detail_price);
            TextView marketCap = view.findViewById(R.id.detail_market_cap);
            ImageView image = view.findViewById(R.id.detail_image);

            name.setText(cryptocurrency.getName());
            description.setText(cryptocurrency.getDescription());
            price.setText(String.format("Цена: %s", cryptocurrency.getPrice()));
            marketCap.setText(String.format("Капитализация: %s", cryptocurrency.getMarketCap()));

            Glide.with(this)
                    .load(cryptocurrency.getImage())
                    .placeholder(android.R.drawable.ic_dialog_info)
                    .error(android.R.drawable.stat_notify_error)
                    .into(image);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error in onCreateView", e);
            getParentFragmentManager().popBackStack();
        }

        return view;
    }
}