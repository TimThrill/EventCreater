package com.cong.eventcreater.model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by admin on 17/09/2016.
 */
public class LoadAllEvents extends AsyncTask<Context, Void, HashMap<String, Event>> {
    private final String TAG = "LoadAllEvents";
    Context context;

    public LoadAllEvents(Context context) {
        this.context = context;
    }

    @Override
    protected HashMap<String, Event> doInBackground(Context... params) {
        // Create database
        EventDbHelper eventDbHelper = new EventDbHelper(params[0], null);
        return null;
    }

    public synchronized HashMap<String, Event> retrieveAllEvents() {
        HashMap<String, Event> events = new HashMap<>();
        EventDbHelper eventDbHelper = new EventDbHelper(context, null);
        SQLiteDatabase db = eventDbHelper.getReadableDatabase();

        Cursor c = db.query(EventDbHelper.tblEventName, null, null, null, null, null, null);
        while(c.moveToNext()) {
            String eventId = c.getString(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.EVENT_ID));

            // Get all attendees of this event
            String[] projection = {EventDbHelper.tblAttendee.ATTENDEE};
            String[] selectionArgs = {eventId};
            Cursor cursorAttendees = db.query(EventDbHelper.tblAttendeeName,
                    projection,
                    EventDbHelper.tblAttendee.EVENT_ID + " = ?",
                    selectionArgs,
                    null,
                    null,
                    null);

            if (!events.containsKey(eventId)) {
                events.put(eventId, new Event(eventId,
                        c.getString(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.TITLE)),
                        c.getLong(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.START_DATE)),
                        c.getLong(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.END_DATE)),
                        c.getString(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.VENUE)),
                        c.getDouble(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.LONGITUDE)),
                        c.getDouble(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.LATITUDE)),
                        c.getString(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.NOTE)),
                        null,
                        c.getInt(c.getColumnIndexOrThrow(EventDbHelper.tblEvent.SAFETY_TIME))));
            } else {
                Log.e(TAG, "Redundant Event: " + eventId);
            }
            while(cursorAttendees.moveToNext()) {
                int attendeeId = cursorAttendees.getInt(cursorAttendees.getColumnIndexOrThrow(EventDbHelper.tblAttendee.ATTENDEE));
                // Get attendee
                String[] proj = {ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.LOOKUP_KEY,
                        Build.VERSION.SDK_INT
                                >= Build.VERSION_CODES.HONEYCOMB ?
                                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                                ContactsContract.Contacts.DISPLAY_NAME};
                String selection = ContactsContract.Contacts._ID + "=" + attendeeId;

                Cursor contactCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        proj,
                        selection,
                        null,
                        null);
                contactCursor.moveToFirst();
                events.get(eventId).addAttendee(new Person(attendeeId, contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))));
            }
        }
        Log.d(TAG, "Event NUM: " + events.size());
        db.releaseReference();
        return events;
    }
}
