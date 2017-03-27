package com.cong.eventcreater.model;

import android.content.ContentValues;

/**
 * Created by admin on 20/09/2016.
 */

public class Database {
    public static ContentValues getEventContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventDbHelper.tblEvent.EVENT_ID, event.getId());
        values.put(EventDbHelper.tblEvent.TITLE, event.getTitle());
        values.put(EventDbHelper.tblEvent.START_DATE,
                event.getStartDate() == null ? null : event.getStartDate().getTimeInMillis());
        values.put(EventDbHelper.tblEvent.END_DATE,
                event.getEndDate() == null ? null : event.getEndDate().getTimeInMillis());
        values.put(EventDbHelper.tblEvent.LATITUDE, event.getLatitude());
        values.put(EventDbHelper.tblEvent.LONGITUDE, event.getLongitude());
        values.put(EventDbHelper.tblEvent.VENUE, event.getVenue());
        values.put(EventDbHelper.tblEvent.NOTE, event.getNote());
        values.put(EventDbHelper.tblEvent.SAFETY_TIME, event.getSaftyTime());
        return values;
    }
}
