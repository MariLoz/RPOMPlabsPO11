package by.rpomp.lab_3.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String dbPath;
    private static final String dbName = "notes.db";
    private static final String tableName = "notes";
    private Context myContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.myContext = context;
        //dbPath = context.getFilesDir().getPath() + dbName;
        dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + dbName;
    }
    public DatabaseHelper(Context context) {
        super(context, dbName, null, 1);
        this.myContext=context;
        dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + dbName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableRequest = String.format(
                "CREATE TABLE IF NOT EXISTS %s (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT)",
                tableName
        );
        db.execSQL(createTableRequest);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropRequest = String.format(
                "DROP TABLE IF EXISTS %s",
                tableName
        );
        db.execSQL(dropRequest);
        onCreate(db);
    }

    public void create_db(){
        File file = new File(dbPath);
        if (!file.exists()) {
            try(InputStream myInput = myContext.getAssets().open(dbName);
                OutputStream myOutput = new FileOutputStream(dbPath)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
                myOutput.flush();
            }
            catch(IOException ex){
                Log.d("DatabaseHelper", ex.getMessage());
            }
        }
    }

    public SQLiteDatabase open() throws SQLException {
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
}
