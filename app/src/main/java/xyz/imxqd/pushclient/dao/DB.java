package xyz.imxqd.pushclient.dao;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by imxqd on 2017/3/26.
 */

public class DB {
    private static DB db;
    private DBOpenHelper heler;
    private DB(Application context) {
        heler = new DBOpenHelper(context);
    }

    public synchronized static void init(Application context) {
        if (db == null) {
            db = new DB(context);
        }
    }

    public static int save(long time, String content) {
        SQLiteDatabase database = db.heler.getWritableDatabase();
        ContentValues values = new ContentValues(2);
        values.put("time", time);
        values.put("content", content);
        return (int) database.insert("DMessage", null, values);
    }

    public static void delete(int id) {
        SQLiteDatabase database = db.heler.getWritableDatabase();
        database.delete("DMessage", "id=" + id, null);
    }

    public static LinkedList<DMessage> list() {
        SQLiteDatabase database = db.heler.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM DMessage ORDER BY time DESC;", null);
        LinkedList<DMessage> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            DMessage msg = new DMessage();
            msg.setId(cursor.getInt(cursor.getColumnIndex("id")));
            msg.setTime(cursor.getLong(cursor.getColumnIndex("time")));
            msg.setContent(cursor.getString(cursor.getColumnIndex("content")));
            list.add(msg);
        }
        cursor.close();
        return list;
    }
}
