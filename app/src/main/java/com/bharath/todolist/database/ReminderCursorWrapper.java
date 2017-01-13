package com.bharath.todolist.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bharath.todolist.Reminder;
import com.bharath.todolist.database.ReminderDbSchema.ReminderTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderCursorWrapper extends CursorWrapper {
    public ReminderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Reminder getReminder() {
        String uuidString = getString(getColumnIndex(ReminderTable.Cols.UUID));
        String title = getString(getColumnIndex(ReminderTable.Cols.TITLE));
        long date = getLong(getColumnIndex(ReminderTable.Cols.DATE));
        int isCompleted = getInt(getColumnIndex(ReminderTable.Cols.COMPLETED));
        String category = getString(getColumnIndex(ReminderTable.Cols.CATEGORY));

        Reminder reminder = new Reminder(UUID.fromString(uuidString));
        reminder.setTitle(title);
        reminder.setDate(new Date(date));
        reminder.setCompleted(isCompleted != 0);
        reminder.setCategory(category);

        return reminder;
    }

}
