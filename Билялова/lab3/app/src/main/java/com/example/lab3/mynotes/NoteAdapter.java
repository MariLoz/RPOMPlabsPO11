package com.example.lab3.mynotes;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cursoradapter.widget.CursorAdapter;
import com.bumptech.glide.Glide;
import com.example.lab3.R;

public class NoteAdapter extends CursorAdapter {

    public NoteAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView idView = view.findViewById(R.id.noteId);  // Новый текст для ID
        TextView textView = view.findViewById(R.id.noteText);
        ImageView imageView = view.findViewById(R.id.noteImage);

        // Берем `_id`, а не `id`
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));

        idView.setText("№ " + id);  // Добавляем номер к продукту
        textView.setText(name);

        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(context).load(imagePath).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }
    }
}
