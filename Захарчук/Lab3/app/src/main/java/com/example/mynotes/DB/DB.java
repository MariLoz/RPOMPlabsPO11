package com.example.mynotes.DB;

import static com.example.mynotes.DB.DBHelper.COLUMN_DESCRIPTION;
import static com.example.mynotes.DB.DBHelper.COLUMN_ID;
import static com.example.mynotes.DB.DBHelper.TABLE_NOTES;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        this.mCtx = ctx;
    }

    public void open() {
        mDBHelper = new DBHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
        Cursor cursor = getAllData();
        if (cursor.getCount() < 20) {
            fillInitialData();
        }
        cursor.close();
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    private void fillInitialData() {
        for (int i = 1; i <= 20; i++) {
            addNote("Note #" + i);
        }
    }

    public Cursor getAllData() {
        return mDB.query(TABLE_NOTES, null, null, null, null, null, null);
    }

    public void addNote(String description) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DESCRIPTION, description);
        mDB.insert(TABLE_NOTES, null, cv);
    }

    public void updateNote(long id, String description) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DESCRIPTION, description);
        mDB.update(TABLE_NOTES, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteNote(long id) {
        mDB.delete(TABLE_NOTES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
}