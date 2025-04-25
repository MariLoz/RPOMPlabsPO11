package com.example.lab1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
public class BenchmarkAdapter extends ArrayAdapter<Benchmark>{
    private LayoutInflater inflater;
    private int layout;
    private List<Benchmark> benchmarks;

    public BenchmarkAdapter(@NonNull Context context, int resource, List<Benchmark> benchmarks) {
        super(context, resource, benchmarks);
        this.benchmarks = benchmarks;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Benchmark benchmark = benchmarks.get(position);

        viewHolder.image.setImageResource(benchmark.getVendor());
        viewHolder.name.setText(benchmark.getDevice());
        viewHolder.score.setText(benchmark.getScore());

        return convertView;
    }
    private class ViewHolder{
        final ImageView image;
        final TextView name, score;

        ViewHolder(View view)
        {
            image= view.findViewById(R.id.vendor_logo);
            name = view.findViewById(R.id.name);
            score = view.findViewById(R.id.median_score);
        }
    }
}
