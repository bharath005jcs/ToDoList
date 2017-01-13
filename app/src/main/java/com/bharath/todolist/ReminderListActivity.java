package com.bharath.todolist;

import android.support.v4.app.Fragment;

public class ReminderListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ReminderListFragment();
    }

}
