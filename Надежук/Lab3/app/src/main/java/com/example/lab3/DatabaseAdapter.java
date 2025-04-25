package com.example.lab3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private final Context context;

    public DatabaseAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        try {
            dbHelper.createDataBase();
        } catch (IOException e) {
            throw new Error("Unable to create database");
        }
        dbHelper.openDataBase();
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    @NonNull
    private Cursor getAllEntries(){
        String[] columns = new String[] {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_DESCRIPTION};
        return database.query(DatabaseHelper.TABLE, columns, null, null, null, null, null);
    }

    public List<Note> getNotes(){
        ArrayList<Note> notes = new ArrayList<>();
        Cursor cursor = getAllEntries();
        while (cursor.moveToNext()){
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
            notes.add(new Note(id, description));
        }
        cursor.close();
        return notes;
    }

    public long getCount(){
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Note getNote(long id){
        Note note = null;
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
        if(cursor.moveToFirst()){
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
            note = new Note(id, description);
        }
        cursor.close();
        return note;
    }

    public void insert(@NonNull Note note){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, note.getDescription());
        database.insert(DatabaseHelper.TABLE, null, cv);
    }

    public long delete(long noteId){
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(noteId)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(@NonNull Note note){
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + note.getId();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_DESCRIPTION, note.getDescription());
        return database.update(DatabaseHelper.TABLE, cv, whereClause, null);
    }
}
