package com.example.rpomp_l3;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesAdapter extends CursorAdapter {

    public NotesAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewId = view.findViewById(R.id.textViewId);
        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewDescription);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        ImageView imageViewImportant = view.findViewById(R.id.imageViewImportant); // Новое поле для звездочки

        long id = cursor.getLong(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_DESCRIPTION));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_DATE));
        int isImportant = cursor.getInt(cursor.getColumnIndexOrThrow(NotesDbHelper.COLUMN_IS_IMPORTANT)); // Получаем статус важности

        textViewId.setText(String.valueOf(id));
        textViewTitle.setText(title);
        textViewDescription.setText(description);
        textViewDate.setText(date);

        // Отображаем звездочку, если заметка важная
        if (isImportant == 1) {
            imageViewImportant.setVisibility(View.VISIBLE);
        } else {
            imageViewImportant.setVisibility(View.GONE);
        }
    }
}