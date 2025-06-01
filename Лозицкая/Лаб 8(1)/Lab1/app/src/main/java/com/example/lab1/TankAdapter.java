package com.example.lab1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TankAdapter extends RecyclerView.Adapter<TankAdapter.TankViewHolder>{
    private final LayoutInflater inflater;
    private final int layout;
    private final List<Tank> tanks;
    private OnItemClickListener onItemClickListener;

    public TankAdapter(Context context, int resource, List<Tank> tanks) {
        this.tanks = tanks;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TankViewHolder(inflater.inflate(layout, parent, false));
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull TankViewHolder holder, int position) {
        Tank tank = tanks.get(position);
        holder.nameView.setText(tank.getName());
        holder.photoView.setImageResource(tank.getPhotoResource());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return tanks.size();
    }

    public static class TankViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final ImageView photoView;

        TankViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            photoView = itemView.findViewById(R.id.photo);
        }
    }
}
