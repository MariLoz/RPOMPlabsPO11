package com.example.lab3.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2; // Увеличиваем версию БД

    private static final String TABLE_NAME = "notes";
    private static final String COL_ID = "id";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_IMAGE_PATH = "image_path"; // Добавляем столбец для фото

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_IMAGE_PATH + " TEXT)"; // Добавили поле для фото
        db.execSQL(createTable);

        // Добавляем 20 записей с тестовыми названиями продуктов
        for (int i = 1; i <= 20; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_DESCRIPTION, "Продукт " + i);
            values.put(COL_IMAGE_PATH, ""); // Пока без фото
            db.insert(TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_IMAGE_PATH + " TEXT");
        }
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, description, image_path FROM " + TABLE_NAME, null);
    }


    public boolean addNote(String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_IMAGE_PATH, imagePath);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateNote(int id, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_IMAGE_PATH, imagePath);
        return db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

}
