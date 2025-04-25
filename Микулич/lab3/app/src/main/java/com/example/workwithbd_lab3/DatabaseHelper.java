package com.example.workwithbd_lab3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mynotes.db"; // Имя файла БД
    private static final String DATABASE_PATH = "/data/data/com.example.workwithbd_lab3/databases/";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE_URL = "image_url";

    private final Context context;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        if (!checkDatabase()) {
            copyDatabase(context);
        }
        openDatabase();
    }

    // Проверяем, существует ли база в системной папке
    private boolean checkDatabaseExists() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if(dbFile.exists())
        {
            return true;
        }
        dbFile.mkdirs();
        return false;

        //File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        //return dbFile.exists();
    }

    // Копируем базу из assets в системную папку
    private void copyDatabase(Context context) {
        try {
            InputStream myInput = context.getAssets().open(DATABASE_NAME);
            String outFileName = DATABASE_PATH + DATABASE_NAME;
            FileOutputStream myOutput = new FileOutputStream(context.getDatabasePath(DATABASE_NAME));
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Ошибка при копировании БД: " + e.getMessage());
        }
    }

    // Инициализируем БД
//    public void initializeDatabase() {
//        if (!checkDatabaseExists()) {
//            //copyDatabase();
//        }
//        openDatabase();
//    }

    // Открываем БД
    public void openDatabase() {
        if (database == null) {
            database = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Нам не нужно создавать таблицу, так как база уже существует
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Если понадобится обновление БД
    }

    // Получение всех заметок
    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<>();

        if (database != null) {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NOTES, null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                    noteList.add(new Note(id, name, description, imageUrl));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } else {
            Log.e("DatabaseHelper", "Database is not initialized.");
        }
        return noteList;
    }

//    public boolean isDatabaseInitialized() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTES, null);
//        cursor.moveToFirst();
//        int count = cursor.getInt(0);
//        cursor.close();
//        return count >= 20;
//    }


    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try {
            String path = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e("DatabaseHelper", "Database doesn't exist or cannot be opened: " + e.getMessage());
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    public SQLiteDatabase getReadableDatabase() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            Log.e("DatabaseHelper", "Error opening database: " + e.getMessage());
        }
        return db;
    }

    public void insertNote(String name, String description, String imageUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("image_url", imageUrl);
        db.insert("notes", null, values);
        db.close();
    }

}
