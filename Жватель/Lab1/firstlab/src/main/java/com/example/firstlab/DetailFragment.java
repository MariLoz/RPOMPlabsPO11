package com.example.firstlab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        TextView title = view.findViewById(R.id.detailTitle);
        TextView description = view.findViewById(R.id.detailDescription);

        Bundle args = getArguments();
        if (args != null) {
            title.setText(args.getString("title"));
            description.setText(args.getString("description"));
        }

        return view;
    }
}