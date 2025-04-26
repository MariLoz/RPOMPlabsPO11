package com.example.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mynotes.DB.DBHelper;

public class NotesAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private Cursor cursor;
    private int idColIndex;
    private int descriptionColIndex;

    public NotesAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.cursor = cursor;
        if (cursor != null) {
            idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            descriptionColIndex = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);
        }
    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (cursor != null) {
            cursor.moveToPosition(position);
        }
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_note, parent, false);
        }

        if (cursor != null) {
            cursor.moveToPosition(position);

            TextView tvNoteId = convertView.findViewById(R.id.tvNoteId);
            TextView tvNoteDescription = convertView.findViewById(R.id.tvNoteDescription);

            tvNoteId.setText(String.valueOf(cursor.getInt(idColIndex)));
            tvNoteDescription.setText(cursor.getString(descriptionColIndex));
        }

        return convertView;
    }

    public void refreshCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (cursor != null) {
            idColIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
            descriptionColIndex = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);
        }
        notifyDataSetChanged();
    }
}