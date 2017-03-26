package xyz.imxqd.pushclient.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by imxqd on 2017/3/26.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String NAME = "DMessage";
    public static final int VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "CREATE TABLE \"DMessage\" (\n" +
                "\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "\"time\"  INTEGER NOT NULL,\n" +
                "\"content\"  TEXT NOT NULL\n" +
                ");";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
