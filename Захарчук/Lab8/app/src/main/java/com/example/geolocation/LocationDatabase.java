package com.example.geolocation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

public class LocationDatabase {
    private static final String DATABASE_NAME = "LocationDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_LOCATIONS = "locations";
    private SQLiteDatabase database;
    private Context context;

    public LocationDatabase(Context context) {
        this.context = context;
        // Указываем путь к базе данных в /Android/data/com.example.geolocation/
        File dbPath = new File(context.getExternalFilesDir(null), DATABASE_NAME);
        database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);

        // Вывод пути для проверки
        Log.d("LocationDatabase", "Database path: " + dbPath.getAbsolutePath());

        // Создаем таблицу, если она еще не существует
        onCreate(database);
    }

    private void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_LOCATIONS +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude REAL, longitude REAL, timestamp INTEGER)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public void addLocation(double latitude, double longitude) {
        ContentValues values = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("timestamp", System.currentTimeMillis());
        database.insert(TABLE_LOCATIONS, null, values);
    }

    public Cursor getLocations() {
        // Фильтр: данные за последние 7 дней (604800000 мс = 7 дней)
        long weekAgo = System.currentTimeMillis() - 604800000L;
        return database.query(TABLE_LOCATIONS, null, "timestamp >= ?",
                new String[]{String.valueOf(weekAgo)}, null, null, "timestamp ASC");
    }

    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}