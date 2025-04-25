package com.example.workwithbd_lab3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NoteAdapter extends BaseAdapter {
    private Context context;
    private List<Note> noteList;
    private LayoutInflater inflater;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_note, parent, false);
            holder = new ViewHolder();
            holder.imageNote = convertView.findViewById(R.id.imageNote);
            holder.idNote = convertView.findViewById(R.id.idNote);
            holder.nameNote = convertView.findViewById(R.id.nameNote);
            holder.descriptionNote = convertView.findViewById(R.id.descriptionNote);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = noteList.get(position);
        holder.idNote.setText("ID: " + note.getId());
        holder.nameNote.setText(note.getName());
        holder.descriptionNote.setText(note.getDescription());

        // Используем Glide для загрузки изображения
        Glide.with(context)
                .load(note.getImageUrl())
                .placeholder(R.drawable.ic_placeholder) // изображение-заглушка
                .into(holder.imageNote);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageNote;
        TextView idNote;
        TextView nameNote;
        TextView descriptionNote;
    }
}
