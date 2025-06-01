package com.example.lab3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {
    private final Context context;
    private final LayoutInflater inflater;
    private final List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        super(context, R.layout.notes_item, notes);
        this.context = context;
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.notes_item, parent, false);
            holder = new ViewHolder();
            holder.idView = convertView.findViewById(R.id.id);
            holder.descriptionView = convertView.findViewById(R.id.description);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Note note = notes.get(position);
        holder.idView.setText(String.valueOf(note.getId()));
        holder.descriptionView.setText(note.getDescription());
        return convertView;
    }

    static class ViewHolder {
        TextView idView, descriptionView;
    }
}
