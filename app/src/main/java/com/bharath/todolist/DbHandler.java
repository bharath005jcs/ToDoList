package com.bharath.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bharath on 13-Jan-2017.
 */

public class DbHandler extends SQLiteOpenHelper {

    private static final int Db_Version = 1;
    private static final String Db_Name = "users";

    private static final String Table_Name = "user";

    private static final String User_id = "id";
    private static final String User_name = "name";

    public DbHandler(Context context) {
        super(context, Db_Name, null, Db_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String Create_Table = "CREATE TABLE " + Table_Name + "(" + User_id
                + " INTEGER PRIMARY KEY," + User_name + " TEXT" + ")";
        db.execSQL(Create_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    public void addUser(User usr) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(User_name, usr.getName());

        db.insert(Table_Name, null, cv);
        db.close();
    }

    public List<String> getAllUsers() {
        List<String> userlist = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM user", null);
        if (cursor.moveToFirst()) {
            do {
                userlist.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return userlist;
    }
}