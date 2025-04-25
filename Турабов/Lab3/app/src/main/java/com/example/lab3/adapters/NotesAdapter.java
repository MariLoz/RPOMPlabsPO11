package com.example.lab3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lab3.Note;
import com.example.lab3.R;

import java.util.List;

public class NotesAdapter extends ArrayAdapter<Note> {

    private LayoutInflater inflater;
    private int layout;
    private List<Note> notes;

    public NotesAdapter(@NonNull Context context, int resource, List<Note> notes) {
        super(context, resource, notes);
        this.notes = notes;
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
        Note note = notes.get(position);

        viewHolder.id.setText(((Integer)note.get_id()).toString());
        viewHolder.title.setText(note.getNoteTitle());
        viewHolder.name.setText(note.getAuthorName());
        viewHolder.task.setText(note.getMainTask());

        return convertView;
    }
    private class ViewHolder{
        final TextView id, title, name, task;

        ViewHolder(View view)
        {
            id = view.findViewById(R.id.note_id);
            title = view.findViewById(R.id.note_title);
            name = view.findViewById(R.id.note_author);
            task = view.findViewById(R.id.note_task);
        }
    }
}
