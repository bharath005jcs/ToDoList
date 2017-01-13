package com.bharath.todolist;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Bharath on 11-Jan-2017.
 */

public class Reminder {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mCompleted;
    private String mCategory;

    public Reminder() {
        this(UUID.randomUUID());
    }

    public Reminder(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isCompleted() {
        return mCompleted;
    }

    public void setCompleted(boolean completed) {
        mCompleted = completed;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
