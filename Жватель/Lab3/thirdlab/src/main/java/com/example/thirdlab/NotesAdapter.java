package com.example.thirdlab;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;
    private int noteNumberIndex;
    private int descriptionIndex;

    public NotesAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.inflater = LayoutInflater.from(context);
        if (cursor != null) {
            noteNumberIndex = cursor.getColumnIndex("note_number");
            descriptionIndex = cursor.getColumnIndex("description");
        }
    }

    // Метод для обновления курсора
    public void changeCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close(); // Закрываем старый курсор
        }
        this.cursor = newCursor;
        if (newCursor != null) {
            noteNumberIndex = newCursor.getColumnIndex("note_number");
            descriptionIndex = newCursor.getColumnIndex("description");
        }
        notifyDataSetChanged(); // Уведомляем адаптер об изменении данных
    }

    @Override
    public int getCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        if (cursor != null && cursor.moveToPosition(position)) {
            TextView text1 = view.findViewById(android.R.id.text1);
            TextView text2 = view.findViewById(android.R.id.text2);
            text1.setText("Note #" + cursor.getInt(noteNumberIndex));
            text2.setText(cursor.getString(descriptionIndex));
        }
        return view;
    }

    @Override
    protected void finalize() throws Throwable {
        if (cursor != null) {
            cursor.close();
        }
        super.finalize();
    }
}