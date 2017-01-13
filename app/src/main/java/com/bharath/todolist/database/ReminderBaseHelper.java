package com.bharath.todolist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bharath.todolist.database.ReminderDbSchema.ReminderTable;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "ReminderBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "reminderBase.db";

    public ReminderBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ReminderTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ReminderTable.Cols.UUID + ", " +
                //ReminderTable.Cols.TITLE + " not null, " +
                ReminderTable.Cols.TITLE + ", " +
                ReminderTable.Cols.DATE + ", " +
                ReminderTable.Cols.CATEGORY + ", " +
                ReminderTable.Cols.COMPLETED +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReminderTable.NAME);

        // Create tables again
        onCreate(db);
    }
}
