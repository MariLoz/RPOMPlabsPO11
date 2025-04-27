package com.example.thirdlab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {
    private static final String DB_TABLE = "notes";
    private final Context mCtx;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    public void open() {
        mDBHelper = new DBHelper(mCtx);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }
    

    public void writeInitialData() {
        Cursor cursor = mDB.query(DB_TABLE, null, null, null, null, null, null);
        if (cursor.getCount() < 20) {
            mDB.delete(DB_TABLE, null, null); // Очистка таблицы
            int i = 1;
            addNote("Жватель Станислав");
            i++;
            while (i <= 25) {
                addNote("Note " + i + " description"); // Автоматическое присвоение номера
                i++;
            }
        }
        cursor.close();
    }

    // Обновленный метод addNote с автоматическим номером
    public void addNote(String description) {
        // Получаем максимальный note_number
        Cursor cursor = mDB.rawQuery("SELECT MAX(note_number) FROM " + DB_TABLE, null);
        int newNoteNumber = 1; // Значение по умолчанию, если таблица пуста
        if (cursor.moveToFirst()) {
            int maxNoteNumber = cursor.getInt(0);
            newNoteNumber = maxNoteNumber + 1;
        }
        cursor.close();

        // Добавляем новую запись
        ContentValues cv = new ContentValues();
        cv.put("note_number", newNoteNumber);
        cv.put("description", description);
        mDB.insert(DB_TABLE, null, cv);
    }

    public void updateNote(int noteNumber, String description) {
        ContentValues cv = new ContentValues();
        cv.put("description", description);
        mDB.update(DB_TABLE, cv, "note_number = ?", new String[]{String.valueOf(noteNumber)});
    }

    public void deleteNote(int noteNumber) {
        mDB.delete(DB_TABLE, "note_number = ?", new String[]{String.valueOf(noteNumber)});
    }

    public Cursor getAllNotes() {
        return mDB.query(DB_TABLE, null, null, null, null, null, "note_number ASC");
    }
}

