package by.rpomp.lab_3.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class Database {
    private static final String dbName = "notes.db";
    private static final String tableName = "notes";
    private DatabaseHelper databaseHelper;
    private final Context context;
    private SQLiteDatabase database;

    public Database(Context context) {
        this.context = context;
    }

    public void openConnection() {
        String downloadsPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        String dbPath = downloadsPath + "/" + dbName;
        databaseHelper = new DatabaseHelper(context, dbPath, null, 1);
        database = databaseHelper.getWritableDatabase();
    }

    public void closeConnection() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    public Cursor getAllData() {
        return database.query(tableName, null, null, null, null, null, null);
    }

    public void addValue(String noteDescription) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", noteDescription);
        database.insert(tableName, null, contentValues);
    }

    public void updateValue(int noteId, String noteDescription) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("description", noteDescription);
        database.update(tableName, contentValues, "id = ?", new String[]{String.valueOf(noteId)});
    }

    public void deleteValue(int id) {
        database.delete(tableName, "id = " + id, null);
    }

    public void deleteAll() {
        database.delete(tableName, null, null);
    }
}
