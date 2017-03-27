package com.cong.eventcreater.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.cong.eventcreater.model.Database;
import com.cong.eventcreater.model.Event;
import com.cong.eventcreater.model.EventDbHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 17/09/2016.
 */
public class InsertEvent extends AsyncTask<Event, Void, Void> {
    private Context context;

    public InsertEvent(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Event... params) {
        Event event = params[0];
        EventDbHelper eventDbHelper = new EventDbHelper(this.context, null);
        SQLiteDatabase db = eventDbHelper.getWritableDatabase();
        ContentValues values = Database.getEventContentValues(event);
        db.insert(EventDbHelper.tblEventName, null, values);

        HashMap<Integer, Person> attendees = event.getAttendees();
        for(Map.Entry<Integer, Person> attendee : attendees.entrySet())
        {
            ContentValues attendeeValue = new ContentValues();
            attendeeValue.put(EventDbHelper.tblAttendee.EVENT_ID, event.getId());
            attendeeValue.put(EventDbHelper.tblAttendee.ATTENDEE, attendee.getKey());
            db.insert(EventDbHelper.tblAttendeeName, null, attendeeValue);
        }
        // Release the db object
        db.releaseReference();
        return null;
    }
}
