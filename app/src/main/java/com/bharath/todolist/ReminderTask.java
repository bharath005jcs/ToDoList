package com.bharath.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.bharath.todolist.database.ReminderBaseHelper;
import com.bharath.todolist.database.ReminderCursorWrapper;
import com.bharath.todolist.database.ReminderDbSchema.ReminderTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderTask {
    private static ReminderTask sReminderTask;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private ReminderTask(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ReminderBaseHelper(mContext)
                .getWritableDatabase();
    }

    public static ReminderTask get(Context context) {
        if (sReminderTask == null) {
            sReminderTask = new ReminderTask(context);
        }
        return sReminderTask;
    }

    public void addReminder(Reminder r) {
        ContentValues values = getContentValues(r);

        mDatabase.insert(ReminderTable.NAME, null, values);
    }

    public List<Reminder> getReminders() {
        List<Reminder> reminders = new ArrayList<>();

        ReminderCursorWrapper cursor = queryReminders(null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            reminders.add(cursor.getReminder());
            cursor.moveToNext();
        }
        cursor.close();

        return reminders;
    }

    public Reminder getReminder(UUID id) {
        ReminderCursorWrapper cursor = queryReminders(
                ReminderTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getReminder();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Reminder reminder) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, reminder.getPhotoFilename());
    }

    public void updateReminder(Reminder reminder) {
        String uuidString = reminder.getId().toString();
        ContentValues values = getContentValues(reminder);

        mDatabase.update(ReminderTable.NAME, values,
                ReminderTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public void deleteReminder(UUID reminderId) {
        String uuidString = reminderId.toString();
        mDatabase.delete(ReminderTable.NAME, ReminderTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private static ContentValues getContentValues(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(ReminderTable.Cols.UUID, reminder.getId().toString());
        values.put(ReminderTable.Cols.TITLE, reminder.getTitle());
        values.put(ReminderTable.Cols.DATE, reminder.getDate().getTime());
        values.put(ReminderTable.Cols.COMPLETED, reminder.isCompleted() ? 1 : 0);
        values.put(ReminderTable.Cols.CATEGORY, reminder.getCategory());

        return values;
    }

    private ReminderCursorWrapper queryReminders(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ReminderTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new ReminderCursorWrapper(cursor);
    }
}
