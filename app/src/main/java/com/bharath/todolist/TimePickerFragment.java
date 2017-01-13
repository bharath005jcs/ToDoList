package com.bharath.todolist;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.bharath.todolist.ReminderFragment.RQS_1;

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "com.bharath.todolist.date";

    private static final String ARG_DATE = "DATE";
    private static final String ARG_TITLE = "TITLE";

    String title;

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void newTitleIntent(String title) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TITLE, title);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        title = (String) getArguments().get(ARG_TITLE);
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        int hour;
                        int minute;
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            hour = mTimePicker.getHour();
                            minute = mTimePicker.getMinute();
                        } else {
                            hour = mTimePicker.getCurrentHour();
                            minute = mTimePicker.getCurrentMinute();
                        }
                        calendar.set(Calendar.HOUR, hour);
                        calendar.set(Calendar.MINUTE, minute);

                        Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                        Calendar calSet = (Calendar) calendar.clone();

                        calSet.set(Calendar.HOUR_OF_DAY, hour);
                        calSet.set(Calendar.MINUTE, minute);
                        calSet.set(Calendar.SECOND, 0);
                        calSet.set(Calendar.MILLISECOND, 0);

                        if (calSet.compareTo(calendar) <= 0) {

                            calSet.add(Calendar.DATE, 0);
                        }


                        setAlarm(calSet);
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);

    }

    private void setAlarm(Calendar targetCal) {

        //
        Toast.makeText(getActivity(), "Alarm is set at " + targetCal.getTime(),
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("Title", title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
                pendingIntent);

    }

}
