package com.bharath.todolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

//import android.support.annotation.Nullable;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderPagerActivity extends AppCompatActivity {
    private static final String EXTRA_REMINDER_ID =
            "com.bharath.todolist.reminder_id";

    private ViewPager mViewPager;
    private List<Reminder> mReminders;

    public static Intent newIntent(Context packageContext, UUID reminderId) {
        Intent intent = new Intent(packageContext, ReminderPagerActivity.class);
        intent.putExtra(EXTRA_REMINDER_ID, reminderId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reminder_pager);

        UUID reminderId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_REMINDER_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_reminder_pager_view_pager);
        mReminders = ReminderTask.get(this).getReminders();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Reminder reminder = mReminders.get(position);
                return ReminderFragment.newInstance(reminder.getId());
            }

            @Override
            public int getCount() {
                return mReminders.size();
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Reminder reminder = mReminders.get(position);
                if (reminder.getTitle() != null) {
                    setTitle(reminder.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        for (int i = 0; i < mReminders.size(); i++) {
            if (mReminders.get(i).getId().equals(reminderId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
