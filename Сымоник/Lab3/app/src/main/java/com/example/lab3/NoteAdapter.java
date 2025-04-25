package com.example.lab3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class NoteAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;
    private LayoutInflater inflater;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public Object getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return notes.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_note, parent, false);
        }

        TextView numberView = convertView.findViewById(R.id.noteNumber);
        TextView titleView = convertView.findViewById(R.id.noteTitle);
        TextView descView = convertView.findViewById(R.id.noteDescription);

        Note note = notes.get(position);
        numberView.setText(String.valueOf(note.getId()));
        titleView.setText(note.getTitle());
        descView.setText(note.getDescription());

        return convertView;
    }
}
