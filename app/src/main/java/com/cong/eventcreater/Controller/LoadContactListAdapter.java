package com.cong.eventcreater.Controller;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


import com.cong.eventcreater.R;
import com.cong.eventcreater.model.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by admin on 12/08/2016.
 */
public class LoadContactListAdapter extends CursorAdapter {

    private LayoutInflater mInflater; // Stores the layout inflater
    private HashMap<Integer, Boolean> cbxStatus;    // Store the status of checkbox

    private class ViewHolder {
        TextView contactName;
        CheckBox cbx;
    }

    public HashMap<Integer, Boolean> getCbxStatus() {
        return cbxStatus;
    }

    public void setCbxStatus(HashMap<Integer, Boolean> cbxStatus) {
        this.cbxStatus = cbxStatus;
    }

    public LoadContactListAdapter(Context context, Cursor c, int flags, HashMap<Integer, Person> attendees)
    {
        this(context, c, flags);
        cbxStatus = new HashMap<Integer, Boolean>();
        refreshAttendees(attendees);
    }

    public LoadContactListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflates the list item layout.
        final View itemLayout =
                mInflater.inflate(R.layout.contacts_list_item, parent, false);

        // Creates a new ViewHolder in which to store handles to each view resource. This
        // allows bindView() to retrieve stored references instead of calling findViewById for
        // each instance of the layout.
        final ViewHolder holder = new ViewHolder();
        holder.contactName = (TextView) itemLayout.findViewById(android.R.id.text1);
        holder.cbx = (CheckBox) itemLayout.findViewById(R.id.contact_checkbox);

        // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
        // available to bindView and other methods that receive a handle to the item view.
        itemLayout.setTag(holder);

        // Returns the item layout view
        return itemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cur) {
        // Gets handles to individual view resources
        final ViewHolder holder = (ViewHolder) view.getTag();

        String contactName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

        holder.contactName.setText(contactName);

        int contactId = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
        if(cbxStatus.containsKey(contactId)) {
            holder.cbx.setChecked(cbxStatus.get(contactId));
        }
        else
        {
            holder.cbx.setChecked(false);
        }
    }

    /**
     * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
     * CursorAdapter.
     */
    @Override
    public Cursor swapCursor(Cursor newCursor) {
        return super.swapCursor(newCursor);
    }

    /**
     * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
     * getCount returns zero. As a result, no test for Cursor == null is needed.
     */
    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    public void refreshAttendees(HashMap<Integer, Person> attendees) {
        // Initialise the status to true that attendees already chose previously
        for(Map.Entry<Integer, Person> attendee : attendees.entrySet()) {
            int key = attendee.getKey();
            cbxStatus.put(attendee.getKey(), true);
        }
    }
}
