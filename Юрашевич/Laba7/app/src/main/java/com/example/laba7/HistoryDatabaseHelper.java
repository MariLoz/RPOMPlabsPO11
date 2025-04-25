package com.example.laba7;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "media_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_FORMAT = "format";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        context.deleteDatabase(DATABASE_NAME); // Пересоздаем БД при каждом запуске
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_HISTORY + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_FORMAT + " TEXT," +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void addToHistory(String type, String name, String format) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, type);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_FORMAT, format);
        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public List<HistoryItem> getHistoryByType(String type) {
        List<HistoryItem> history = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY,
                new String[]{COLUMN_NAME, COLUMN_FORMAT, COLUMN_TIMESTAMP},
                COLUMN_TYPE + " = ?",
                new String[]{type},
                null, null,
                COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                history.add(new HistoryItem(
                        cursor.getString(0), // name
                        cursor.getString(1), // format
                        cursor.getString(2)  // timestamp
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return history;
    }

    public static class HistoryItem {
        public String name;
        public String format;
        public String timestamp;

        public HistoryItem(String name, String format, String timestamp) {
            this.name = name;
            this.format = format;
            this.timestamp = timestamp;
        }
    }
}