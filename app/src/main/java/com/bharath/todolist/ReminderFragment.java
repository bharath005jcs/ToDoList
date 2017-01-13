package com.bharath.todolist;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.bharath.todolist.PictureUtils.getScaledBitmap;
import static com.bharath.todolist.R.id.spinner;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class ReminderFragment extends Fragment {

    private static final String ARG_REMINDER_ID = "reminder_id";
    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_LOAD_IMAGE = 3;

    private Reminder mReminder;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button shareTask;
    private Button mTimeButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Spinner mSpinner;
    private Button mAddButton;
    private EditText mAddEditText;

    List<String> groups = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DbHandler db;
    final static int RQS_1 = 1;

    public static ReminderFragment newInstance(UUID reminderId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_REMINDER_ID, reminderId);

        ReminderFragment fragment = new ReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID reminderId = (UUID) getArguments().getSerializable(ARG_REMINDER_ID);
        mReminder = ReminderTask.get(getActivity()).getReminder(reminderId);
        mPhotoFile = ReminderTask.get(getActivity()).getPhotoFile(mReminder);
    }

    @Override
    public void onResume() {
        super.onResume();
        //handleNotification();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mTitleField.getText().toString().equals("")) {
            UUID reminderId = mReminder.getId();
            ReminderTask.get(getActivity()).deleteReminder(reminderId);
        } else {
            // Log.d("PAUSE", "onPause() called for" + i + "time");
            //handleNotification();
            ReminderTask.get(getActivity()).updateReminder(mReminder);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reminder, container, false);

        mTitleField = (EditText) v.findViewById(R.id.reminder_title);
        mTitleField.setText(mReminder.getTitle());
        mSpinner = (Spinner) v.findViewById(spinner);
        mAddButton = (Button) v.findViewById(R.id.add_category_button);
        mAddEditText = (EditText) v.findViewById(R.id.add_category_text);
        db = new DbHandler(getActivity());

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mAddEditText.getText().toString();
                if (username.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "Please Enter Category", Toast.LENGTH_SHORT).show();
                } else {
                    db.addUser(new User(username));
                    prepareData();
                    mAddEditText.setText("");
                }
            }
        });

        prepareData();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mReminder.setCategory(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReminder.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.reminder_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mReminder.getDate());
                dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox) v.findViewById(R.id.reminder_completed);
        mSolvedCheckbox.setChecked(mReminder.isCompleted());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mReminder.setCompleted(isChecked);
            }
        });

        shareTask = (Button) v.findViewById(R.id.share_task);
        shareTask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.reminder_task_status));
                i = Intent.createChooser(i, getString(R.string.send_report));

                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mTimeButton = (Button) v.findViewById(R.id.reminder_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mReminder.getDate());
                TimePickerFragment.newTitleIntent(mReminder.getTitle());
                dialog.setTargetFragment(ReminderFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

//        if (mReminder.getCategory() != null) {
//            mSpinner.setText(mReminder.getCategory());
//        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mTimeButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.reminder_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(captureImage, REQUEST_PHOTO);
                registerForContextMenu(mPhotoButton);
                v.showContextMenu();
            }
        });


        mPhotoView = (ImageView) v.findViewById(R.id.reminder_photo);
        updatePhotoView();

        return v;
    }

    final int CONTEXT_MENU_VIEW = 1;
    final int CONTEXT_MENU_EDIT = 2;

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Add Picture");
        menu.add(Menu.NONE, CONTEXT_MENU_VIEW, Menu.NONE, "Take Picture");
        menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Choose From Gallery");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case CONTEXT_MENU_VIEW: {

                PackageManager packageManager = getActivity().getPackageManager();

                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                boolean canTakePhoto = mPhotoFile != null &&
                        captureImage.resolveActivity(packageManager) != null;
                mPhotoButton.setEnabled(canTakePhoto);

                if (canTakePhoto) {
                    Uri uri = Uri.fromFile(mPhotoFile);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
            break;
            case CONTEXT_MENU_EDIT: {
                // Edit Action
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_LOAD_IMAGE);

            }
            break;
        }

        return super.onContextItemSelected(item);
    }


    public void prepareData() {
        groups = db.getAllUsers();
        //adapter for spinner
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, groups);
        //attach adapter to spinner
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReminder.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReminder.setDate(date);
            updateDate();
            updateTime();
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        } else if (requestCode == REQUEST_LOAD_IMAGE) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Toast.makeText(getActivity(), "Path: " + picturePath, Toast.LENGTH_SHORT).show();
            Bitmap bitmap = getScaledBitmap(picturePath, getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    private void updateDate() {
        mDateButton.setText(mReminder.getDate().toString());
    }

    private void updateTime() {
        CharSequence formattedTime = DateFormat.format("hh:mm", mReminder.getDate());
        mTimeButton.setText(formattedTime);
    }

    private String getReport() {
        String solvedString = null;
        if (mReminder.isCompleted()) {
            solvedString = getString(R.string.reminder_report_complete);
        } else {
            solvedString = getString(R.string.reminder_report_incomplete);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mReminder.getDate()).toString();
        String suspect = mReminder.getCategory();
        if (suspect == null) {
            suspect = getString(R.string.reminder_no_category);
        } else {
            suspect = getString(R.string.reminder_category, suspect);
        }
        String report = getString(R.string.send_reminder,
                mReminder.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_reminder_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_reminder_action:
                UUID reminderId = mReminder.getId();
                ReminderTask.get(getActivity()).deleteReminder(reminderId);

                Toast.makeText(getActivity(), "Task Deleted", Toast.LENGTH_SHORT).show();
                getActivity().finish();

                return true;

            //case android.R.id.home:

            //  return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

//    private void handleNotification(){
//        Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
//        alarmIntent.putExtra("Task_Title", mReminder.getTitle());
//        alarmIntent.putExtra("Task Date", mReminder.getDate().getHours());
//        //Toast.makeText(getActivity(), "Hour: " + mReminder.getDate().getHours(), Toast.LENGTH_SHORT).show();
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mReminder.getDate().getTime(), 0, pendingIntent);
////        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mReminder.getDate().getTime(), 5000, pendingIntent);
//
//    }

//    private void setAlarm(Calendar targetCal) {
//
//        //
//        Toast.makeText(getActivity(), "Alarm is set at" + targetCal.getTime(),
//                Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                getActivity(), RQS_1, intent, 0);
//        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(),
//                pendingIntent);
//
//    }

}

