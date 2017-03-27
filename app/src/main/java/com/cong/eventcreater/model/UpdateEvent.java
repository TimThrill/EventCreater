package com.cong.eventcreater.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.cong.eventcreater.model.Database;
import com.cong.eventcreater.model.Event;
import com.cong.eventcreater.model.EventDbHelper;
import com.cong.eventcreater.model.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 20/09/2016.
 */
public class UpdateEvent extends AsyncTask<Event, Void, Void> {
    private Context context;
    private final String TAG = "UpdateEvent";
    public UpdateEvent(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Event... params) {
        Event event = params[0];
        EventDbHelper eventDbHelper = new EventDbHelper(this.context, null);
        SQLiteDatabase db = eventDbHelper.getWritableDatabase();
        ContentValues values = Database.getEventContentValues(event);

        // Which row to update, based on the title
        String selection = EventDbHelper.tblEvent.EVENT_ID + " LIKE ?";
        String[] selectionArgs = {event.getId()};

        // Update event table
        int cnt = db.update(EventDbHelper.tblEventName, values, selection, selectionArgs);
        Log.d(TAG, "tblEvent " + cnt + " rows have been updated");

        // Update attendees table
        // First delete all attendees of this event in attendee table
        String deleteSelection = EventDbHelper.tblAttendee.EVENT_ID + " LIKE ?";
        String[] deleteSelectionArgs = {event.getId()};
        int deleteCnt = db.delete(EventDbHelper.tblAttendeeName, deleteSelection, deleteSelectionArgs);
        Log.d(TAG, "tblAttendee " + deleteCnt + " rows have been deleted");
        // Insert attendees into database
        HashMap<Integer, Person> attendees = event.getAttendees();
        for(Map.Entry<Integer, Person> attendee : attendees.entrySet())
        {
            ContentValues attendeeValue = new ContentValues();
            attendeeValue.put(EventDbHelper.tblAttendee.EVENT_ID, event.getId());
            attendeeValue.put(EventDbHelper.tblAttendee.ATTENDEE, attendee.getKey());
            db.insert(EventDbHelper.tblAttendeeName, null, attendeeValue);
        }
        db.releaseReference();
        Log.d(TAG, "Update event finished");
        return null;
    }
}
