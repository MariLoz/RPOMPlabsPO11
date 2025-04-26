package com.example.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MediaHistory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_MEDIA = "media_history";
    private static final String TABLE_PHOTO = "photo_history";
    private static HistoryDatabase instance;

    public static synchronized HistoryDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private HistoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MEDIA + " (id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT, timestamp INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_PHOTO + " (id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT, location TEXT, timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        onCreate(db);
    }

    public void addMediaHistory(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", path);
        values.put("timestamp", System.currentTimeMillis());
        db.insert(TABLE_MEDIA, null, values);
        db.close();
    }

    public void addPhotoHistory(String path, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("path", path);
        values.put("location", location);
        values.put("timestamp", System.currentTimeMillis());
        db.insert(TABLE_PHOTO, null, values);
        db.close();
    }

    public Cursor getMediaHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MEDIA, null, null, null, null, null, "timestamp DESC");
    }

    public Cursor getPhotoHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PHOTO, null, null, null, null, null, "timestamp DESC");
    }
}