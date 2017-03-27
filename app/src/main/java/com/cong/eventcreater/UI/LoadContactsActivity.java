package com.cong.eventcreater.UI;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.cong.eventcreater.Controller.LoadContactListAdapter;
import com.cong.eventcreater.R;
import com.cong.eventcreater.model.Person;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadContactsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private LoadContactListAdapter mCursorAdapter;
    // OK button
    private Button btOk;
    // Cancel button
    private Button btCancel;

    private HashMap<Integer, Person> attendees;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
    {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME

    };
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    public static String mSearchString = "";
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_contacts);

        // Get attendees data from CreateEventActivity
        this.attendees = (HashMap<Integer, Person>)getIntent().getSerializableExtra("Attendees");
        this.mSearchString = "";

        // Sets the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // Hide activity title in toolbar

        // Gets the ListView from the View list of the parent activity
        mContactsList =
                (ListView) findViewById(R.id.contacts_list_view);

        mCursorAdapter = new LoadContactListAdapter(
                this,
                null,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,
                attendees);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);

        // Initializes the loader
        getLoaderManager().initLoader(0, null, this);


        // Set the item click listener to be the current fragment.
        mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("OnContactItemClick: ", "*");

                // Get the Cursor
                Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                // Move to the selected contact
                cursor.moveToPosition(position);
                // Get the _ID value
                mContactId = CONTACT_ID_INDEX;
                // Get the selected LOOKUP KEY
                mContactKey = String.valueOf(LOOKUP_KEY_INDEX);
                // Create the contact's content Uri
                mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
                /*
                 * You can use mContactUri as the content URI for retrieving
                 * the details for a contact.
                 */
                CheckBox cbx = (CheckBox)view.findViewById(R.id.contact_checkbox);

                if(cbx != null) {
                    boolean isChecked = cbx.isChecked();
                    Log.d("Set checkbox", String.valueOf(!isChecked));

                    int attendeeId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String fullName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d("FULL NAME: ", fullName);

                    if(!isChecked)  // Select attendee
                    {
                        // Previous status is unchecked, after clicking it will be selected
                        Person attendee = new Person(attendeeId, fullName);

                        if(!attendees.containsKey(attendeeId))
                        {
                            attendees.put(attendeeId, attendee);
                        }
                        else
                        {
                            Log.d("Attendees conflict", "ID: " + String.valueOf(attendeeId) + " Name: " + fullName);
                        }
                    }
                    else    // Cancel selected attendee
                    {
                        if(attendees.containsKey(attendeeId))
                        {
                            attendees.remove(attendeeId);
                        }
                        else
                        {
                            Log.d("Warning: ", "Attendee not existed");
                        }
                    }
                    cbx.setChecked(!isChecked);
                    mCursorAdapter.getCbxStatus().put(attendeeId, !isChecked);
                }
                else
                {
                    Log.d("Error: ", view.getClass().toString());
                }
            }
        });

        btOk = (Button)findViewById(R.id.contact_ok);
        btCancel = (Button)findViewById(R.id.contact_cancel);
        btOk.setOnClickListener(this);
        btCancel.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_load_contacts, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        // Set text listner
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if ((null != query)) {
                    mSearchString = query;
                    // This implementation of SearchView.OnQueryTextListener restarts the loader when the user's query changes.
                    // The loader needs to be restarted so that it can use the revised search filter to do a new query:
                    getLoaderManager().restartLoader(0, null, LoadContactsActivity.this);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (null != newText) {
                    mSearchString = newText;
                    // This implementation of SearchView.OnQueryTextListener restarts the loader when the user's query changes.
                    // The loader needs to be restarted so that it can use the revised search filter to do a new query:
                    getLoaderManager().restartLoader(0, null, LoadContactsActivity.this);
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Start Loader: ", "**** " + mSearchString);
        /*
         * Makes search string into pattern and
         * stores it in the selection array
         */
        mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View v) {
        int btId = v.getId();
        if(btId == R.id.contact_ok) {    // Click ok button
            Log.d("Button click: ", "OK");
            Intent intentMessage = new Intent();
            intentMessage.putExtra("Attendees_Result", this.attendees);
            if (getParent() == null) {
                setResult(RESULT_OK, intentMessage);
                finish();
            } else {
                getParent().setResult(RESULT_OK, intentMessage);
                getParent().finish();
            }
        }
        else if(btId == R.id.contact_cancel) { // Click cancel button
            finish();
        }
        else {
            Log.d("onClick", "Unknown click");
            finish();
        }
    }
}
