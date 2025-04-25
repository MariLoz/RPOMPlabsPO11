package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CharacterAdapter extends BaseAdapter {
    private final Context context;
    private final List<CharacterItem> characterList;
    private final LayoutInflater inflater;

    public CharacterAdapter(Context context, List<CharacterItem> characterList) {
        this.context = context;
        this.characterList = characterList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return characterList.size();
    }

    @Override
    public CharacterItem getItem(int position) {
        return characterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_character, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CharacterItem item = getItem(position);

        holder.nameTextView.setText(item.getName());
        Glide.with(context)
                .load("file:///android_asset/img/" + item.getThumbnail())
                .into(holder.thumbnailImageView);

        return convertView;
    }

    private static class ViewHolder {
        final TextView nameTextView;
        final ImageView thumbnailImageView;

        ViewHolder(View view) {
            nameTextView = view.findViewById(R.id.nameTextView);
            thumbnailImageView = view.findViewById(R.id.thumbnailImageView);
        }
    }
}
