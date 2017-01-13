package com.bharath.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import static com.bharath.todolist.ReminderPagerActivity.newIntent;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mReminderRecyclerView;
    private ReminderAdapter mAdapter;
    private boolean mSubtitleVisible;
    private TextView mTextView;
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        mReminderRecyclerView = (RecyclerView) view
                .findViewById(R.id.reminder_recycler_view);
        mReminderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTextView = (TextView) view.findViewById(R.id.empty_text_view);
        mButton = (Button) view.findViewById(R.id.empty_view_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = createReminderIntent();
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_reminder_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_reminder:
                Reminder reminder = new Reminder();
                ReminderTask.get(getActivity()).addReminder(reminder);
                Intent intent =
                        newIntent(getActivity(), reminder.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        ReminderTask reminderTask = ReminderTask.get(getActivity());
        int taskCount = reminderTask.getReminders().size();
        String subtitle = getString(R.string.show_count, taskCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        ReminderTask reminderTask = ReminderTask.get(getActivity());
        List<Reminder> reminders = reminderTask.getReminders();

        if (reminders.isEmpty()) {
            mTextView.setVisibility(View.VISIBLE);
            mButton.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
            mButton.setVisibility(View.GONE);
        }

        if (mAdapter == null) {
            mAdapter = new ReminderAdapter(reminders);
            mReminderRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setReminders(reminders);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private Intent createReminderIntent() {
        Reminder reminder = new Reminder();
        ReminderTask.get(getActivity()).addReminder(reminder);
        Intent intent = ReminderPagerActivity.newIntent(getActivity(), reminder.getId());
        return intent;

    }

    private class ReminderHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        private Reminder mReminder;

        public ReminderHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_reminder_title_text_view);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_reminder_completed_check_box);
        }

        public void bindReminder(Reminder reminder) {
            mReminder = reminder;
            mTitleTextView.setText(mReminder.getTitle());
            mSolvedCheckBox.setChecked(mReminder.isCompleted());
        }

        @Override
        public void onClick(View v) {
            Intent intent = newIntent(getActivity(), mReminder.getId());
            startActivity(intent);
        }
    }

    private class ReminderAdapter extends RecyclerView.Adapter<ReminderHolder> {

        private List<Reminder> mReminders;

        public ReminderAdapter(List<Reminder> reminders) {
            mReminders = reminders;
        }

        @Override
        public ReminderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_reminder, parent, false);
            return new ReminderHolder(view);
        }

        @Override
        public void onBindViewHolder(ReminderHolder holder, int position) {
            Reminder reminder = mReminders.get(position);
            holder.bindReminder(reminder);
        }

        @Override
        public int getItemCount() {
            return mReminders.size();
        }

        public void setReminders(List<Reminder> reminders) {
            mReminders = reminders;
        }
    }

}
