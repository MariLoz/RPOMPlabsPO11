package com.example.lab8;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RunAdapter extends ArrayAdapter<Run> {
    public RunAdapter(Context context, List<Run> runs) {
        super(context, 0, runs);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Run run = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.run_item, parent, false);
        }

        TextView tvDistance = convertView.findViewById(R.id.tvDistance);
        TextView tvDuration = convertView.findViewById(R.id.tvDuration);
        TextView tvDate = convertView.findViewById(R.id.tvDate);

        tvDistance.setText(run.getFormattedDistance());
        tvDuration.setText(run.getFormattedDuration());
        tvDate.setText(run.getTimestamp());

        return convertView;
    }
}