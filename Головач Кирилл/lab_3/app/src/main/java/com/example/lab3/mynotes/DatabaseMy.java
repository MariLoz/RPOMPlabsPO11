package com.example.lab3.mynotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseMy extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 2; // Увеличиваем версию БД

    private static final String TABLE_NAME = "notes";
    private static final String COL_ID = "id";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_IMAGE_PATH = "image_path"; // Добавляем столбец для фото

    public DatabaseMy(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " + // Убрали AUTOINCREMENT
                COL_DESCRIPTION + " TEXT, " +
                COL_IMAGE_PATH + " TEXT)"; // Добавили поле для фото
        db.execSQL(createTable);

        // Добавляем 20 тестовых записей с ручным присвоением ID
        for (int i = 1; i <= 20; i++) {
            ContentValues values = new ContentValues();
            values.put(COL_ID, i); // Ручное присвоение ID
            values.put(COL_DESCRIPTION, "Головач Кирилл ПО-11 " + i);
            values.put(COL_IMAGE_PATH, ""); // Пока без фото
            db.insert(TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Удаляем старую таблицу
            onCreate(db); // Создаем новую таблицу
        }
    }

    // Метод для получения следующего ID
    public int getNextId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COL_ID + ") FROM " + TABLE_NAME, null);
        int nextId = 1; // По умолчанию первый ID = 1
        if (cursor.moveToFirst()) {
            int maxId = cursor.getInt(0); // Получаем максимальный ID
            if (maxId != 0) {
                nextId = maxId + 1; // Следующий ID = максимальный ID + 1
            }
        }
        cursor.close();
        return nextId;
    }

    // Получение всех записей
    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id AS _id, description, image_path FROM " + TABLE_NAME, null);
    }

    // Добавление записи с ручным присвоением ID
    public boolean addNote(String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Получаем следующий ID
        int nextId = getNextId();

        ContentValues values = new ContentValues();
        values.put(COL_ID, nextId); // Ручное присвоение ID
        values.put(COL_DESCRIPTION, description);
        values.put(COL_IMAGE_PATH, imagePath);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Удаление записи по ID
    public boolean deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Обновление записи по ID
    public boolean updateNote(int id, String description, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DESCRIPTION, description);
        values.put(COL_IMAGE_PATH, imagePath);
        return db.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(id)}) > 0;
    }
}