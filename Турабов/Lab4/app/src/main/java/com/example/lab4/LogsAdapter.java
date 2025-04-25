package com.example.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class LogsAdapter extends ArrayAdapter<PDFLog> {

    private LayoutInflater inflater;
    private int layout;
    private List<PDFLog> logs;

    public LogsAdapter(@NonNull Context context, int resource, List<PDFLog> logs) {
        super(context, resource, logs);
        this.logs = logs;
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
        PDFLog log = logs.get(position);

        viewHolder.date.setText(log.getDate());
        viewHolder.name.setText(log.getName());


        return convertView;
    }
    private class ViewHolder{
        final TextView name, date;

        ViewHolder(View view)
        {
            name = view.findViewById(R.id.log_name);
            date = view.findViewById(R.id.log_date);
        }
    }
}
