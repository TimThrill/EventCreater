package com.cong.eventcreater.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by admin on 20/09/2016.
 */

public class DeleteEvent extends AsyncTask<Event, Void, Void> {
    private final String TAG = "DeleteEvent";
    private Context context;

    public DeleteEvent(Context context) {
        this.context = context;
    }
    @Override
    protected Void doInBackground(Event... params) {
        Event event = params[0];
        EventDbHelper eventDbHelper = new EventDbHelper(this.context, null);
        SQLiteDatabase db = eventDbHelper.getWritableDatabase();

        // First delete all attendees of this event in attendee table
        String deleteSelection = EventDbHelper.tblAttendee.EVENT_ID + " LIKE ?";
        String[] deleteSelectionArgs = {event.getId()};
        int deleteCnt = db.delete(EventDbHelper.tblAttendeeName, deleteSelection, deleteSelectionArgs);
        Log.d(TAG, "tblAttendee " + deleteCnt + " rows have been deleted");

        // Then delete all events in the event table
        String deleteEventSelection = EventDbHelper.tblEvent.EVENT_ID + " LIKE ?";
        int deleteEventCnt = db.delete(EventDbHelper.tblEventName, deleteEventSelection, deleteSelectionArgs);
        Log.d(TAG, "tblEvent " + deleteEventCnt + " rows have been deleted");
        return null;
    }
}
