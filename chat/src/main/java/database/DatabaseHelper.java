package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String SQLITE = "friends.db3";
    public static DatabaseHelper databaseHelper;
    public DatabaseHelper(Context context, String name,SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public  DatabaseHelper(Context context,String name){
        this(context, name, null, DatabaseAdapter.VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if (databaseHelper == null){
            databaseHelper = new DatabaseHelper(context,SQLITE);
        }
        return  databaseHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseAdapter.INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(DatabaseAdapter.USERNAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}