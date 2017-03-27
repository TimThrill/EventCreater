package com.cong.eventcreater.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by admin on 16/09/2016.
 */
public class EventDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "EventDbHelper";
    private static final String dbName = "Event.db";
    private static final int version = 1;
    private final String typeText = " TEXT";
    private final String typeInteger = " INTEGER";
    private final String typeReal = " REAL";

    public static final String tblEventName = "event";
    public static final String tblAttendeeName = "attendee";

    public static class tblEvent {
        public static final String EVENT_ID = "eventId";
        public static final String TITLE = "title";
        public static final String START_DATE = "startDate"; // The start date of the event
        public static final String END_DATE = "endDate"; // The end date of the event
        public static final String VENUE = "venue"; // The location of the event
        public static final String LONGITUDE = "longitude"; // The longitude of the event adress
        public static final String LATITUDE = "latitude"; // The latitude of the event address
        public static final String NOTE = "note"; // Additional information
        public static final String SAFETY_TIME = "safetyTime";  // How long before notification
    }

    public static class tblAttendee {
        public static final String EVENT_ID = "eventId";
        public static final String ATTENDEE = "attendeeId";
    }

    private final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE "
            + tblEventName
            + " ("
            + tblEvent.EVENT_ID + typeText + ", "
            + tblEvent.TITLE + typeText + ", "
            + tblEvent.START_DATE + typeInteger + ", "
            + tblEvent.END_DATE + typeInteger + ", "
            + tblEvent.VENUE + typeText + ", "
            + tblEvent.LONGITUDE + typeReal + ", "
            + tblEvent.LATITUDE + typeReal + ", "
            + tblEvent.NOTE + typeText + ", "
            + tblEvent.SAFETY_TIME + typeInteger + ", "
            + "PRIMARY KEY(" + tblEvent.EVENT_ID + "));";

    private final String SQL_CREATE_ATTENDEE_TABLE = "CREATE TABLE "
            + tblAttendeeName
            + " ("
            + tblAttendee.EVENT_ID + typeText + ", "
            + tblAttendee.ATTENDEE + typeInteger + ", "
            + "PRIMARY KEY(" + tblAttendee.ATTENDEE + ", " + tblAttendee.EVENT_ID + "));";

    public EventDbHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, dbName, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_ATTENDEE_TABLE);
        Log.d(TAG, SQL_CREATE_EVENT_TABLE);
        Log.d(TAG, SQL_CREATE_ATTENDEE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
