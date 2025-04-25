package com.example.lab8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "running_tracker.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_RUNS = "runs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_POLYLINE = "polyline";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RUNS_TABLE = "CREATE TABLE " + TABLE_RUNS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DISTANCE + " REAL,"
                + COLUMN_DURATION + " INTEGER,"
                + COLUMN_POLYLINE + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_RUNS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUNS);
        onCreate(db);
    }

    public void addRun(Run run) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DISTANCE, run.getDistance());
        values.put(COLUMN_DURATION, run.getDuration());
        values.put(COLUMN_POLYLINE, run.getPolyline());

        db.insert(TABLE_RUNS, null, values);
        db.close();
    }

    public List<Run> getAllRuns() {
        List<Run> runList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RUNS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Run run = new Run();
                run.setId(cursor.getInt(0));
                run.setDistance(cursor.getFloat(1));
                run.setDuration(cursor.getLong(2));
                run.setPolyline(cursor.getString(3));
                run.setTimestamp(cursor.getString(4));

                runList.add(run);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return runList;
    }

    public void deleteRun(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RUNS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}