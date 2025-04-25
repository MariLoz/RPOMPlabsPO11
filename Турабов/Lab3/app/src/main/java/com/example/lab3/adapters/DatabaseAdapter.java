package com.example.lab3.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.lab3.DatabaseHelper;
import com.example.lab3.Note;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    public DatabaseHelper dbHelper;
    public SQLiteDatabase database;

    public DatabaseAdapter(Context context){
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open(){
        dbHelper.create_db();
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    public Cursor getAllEntries(){
        return  database.query(DatabaseHelper.TABLE_NOTES, DatabaseHelper.columns_notes, null, null, null, null, null);
    }

//    public User getUser(long id){
//        User user = null;
//        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
//        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});
//        if(cursor.moveToFirst()){
//            String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
//            int year = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_YEAR));
//            user = new User(id, name, year);
//        }
//        cursor.close();
//        return  user;
//    }

    public long insert(Note note)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_AUTHOR, note.getAuthorName());
        cv.put(DatabaseHelper.COLUMN_TITLE, note.getNoteTitle());
        cv.put(DatabaseHelper.COLUMN_TASK, note.getMainTask());

        return  database.insert(DatabaseHelper.TABLE_NOTES, null, cv);
    }

    public long insertSubtask(long id, String s) {

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NOTEID, id);
        cv.put(DatabaseHelper.COLUMN_SUBTASK, s);

        return  database.insert(DatabaseHelper.TABLE_SUBTASKS, null, cv);
    }

    public long delete(long noteId){

        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(noteId)};
        database.delete(DatabaseHelper.TABLE_NOTES, whereClause, whereArgs);

        String whereClause1 = "noteId = ?";
        return database.delete(DatabaseHelper.TABLE_SUBTASKS, whereClause1, whereArgs);
    }

    public long update(Note note){

        String whereClause = DatabaseHelper.COLUMN_ID + "=" + note.get_id();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_AUTHOR, note.getAuthorName());
        cv.put(DatabaseHelper.COLUMN_TITLE, note.getNoteTitle());
        cv.put(DatabaseHelper.COLUMN_TASK, note.getMainTask());
        return database.update(DatabaseHelper.TABLE_NOTES, cv, whereClause, null);
    }

    public ArrayList<String> getSubtasks(int id) {
        ArrayList<String> subs = new ArrayList<String>();
        String query = String.format("SELECT * FROM %s WHERE %s=?",DatabaseHelper.TABLE_SUBTASKS, DatabaseHelper.COLUMN_NOTEID);
        Cursor cursor = database.rawQuery(query, new String[]{ String.valueOf(id)});

        //String query = String.format("SELECT * FROM %s",DatabaseHelper.TABLE_SUBTASKS);
        //Cursor cursor = database.rawQuery(query, new String[]{});
        Log.println(Log.ERROR, "me",String.valueOf(cursor.getCount()));

        while (cursor.moveToNext()){
            String task = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUBTASK));
            subs.add(task);
        }
        cursor.close();
        return subs;
    }
}