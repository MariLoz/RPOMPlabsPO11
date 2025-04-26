package by.rpomp.lab_3.adapters;

import by.rpomp.lab_3.R;
import by.rpomp.lab_3.entity.Note;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteListAdapter extends BaseAdapter {
    private Context context;
    private List<Note> notes;

    public NoteListAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
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
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.note_item, parent, false);
        }

        TextView textViewNoteId = convertView.findViewById(R.id.noteId);
        TextView textViewNoteDescription = convertView.findViewById(R.id.noteDescription);

        Note note = notes.get(position);
        textViewNoteId.setText("ID: " + note.getId());
        textViewNoteDescription.setText(note.getDescription());
        return convertView;
    }
}
