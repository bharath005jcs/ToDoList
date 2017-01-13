package com.bharath.todolist.database;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderDbSchema {
    public static final class ReminderTable {

        public static final String NAME = "reminders";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String COMPLETED = "completed";
            public static final String CATEGORY = "category";
        }

    }
}
