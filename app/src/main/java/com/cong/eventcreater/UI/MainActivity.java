package com.cong.eventcreater.UI;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cong.eventcreater.Controller.EventListAdapter;
import com.cong.eventcreater.model.AlarmManagerService;
import com.cong.eventcreater.model.Event;
import com.cong.eventcreater.model.Events;
import com.cong.eventcreater.model.REQUEST_CODE;
import com.cong.eventcreater.model.ResultCode;
import com.cong.eventcreater.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private ListView eventListView;
    private ArrayAdapter<Event> adapter;
    private Intent i;
    private boolean isDescendOrder;
    private Events events;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isDescendOrder = false;

        // Sets the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // Hide activity title in toolbar

        events = new Events();
        events.loadAllEvents(this);

        eventListView = (ListView)findViewById(R.id.eventListView);
        if(!isDescendOrder) {
            adapter = new EventListAdapter(this, R.layout.events_list_item, events.getAscendOrder());
        } else {
            adapter = new EventListAdapter(this, R.layout.events_list_item, events.getDescenderOrder());
        }
        eventListView.setAdapter(adapter);

        // Start the background service to update event status frequently
        Intent alaramManagerServiceIntent = new Intent(this, AlarmManagerService.class);
        startService(alaramManagerServiceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_event) {
            String eventId = UUID.randomUUID().toString();
            Event event = new Event(eventId);
            i = new Intent(MainActivity.this, CreateEventActivity.class);
            i.putExtra("Event", event);
            startActivityForResult(i, REQUEST_CODE.CREATE_EVENT);
            return true;
        } else if(id == R.id.calendar_view) {
            Intent i = new Intent(MainActivity.this, CalendarActivity.class);
            startActivityForResult(i, REQUEST_CODE.CALENDAR_VIEW);
            return true;
        } else if(id == R.id.eventsOrder) {
            isDescendOrder = !isDescendOrder;
            if(isDescendOrder) {
                item.setIcon(android.R.drawable.arrow_down_float);
            } else {
                item.setIcon(android.R.drawable.arrow_up_float);
            }
            refreshEventListView();
        } else if(id == R.id.settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if ((requestCode == REQUEST_CODE.CREATE_EVENT)) {
                Event event = (Event) data.getSerializableExtra("EeventResult");
                Log.d("MainActivity", event.toString());
                // Create a new event, add into events set
                events.addEvent(event, this);
                refreshEventListView();
                Log.d("MainActivity", "EVENTS NUMBER: " + events.getEvents().size()
                );
            } else if(requestCode == REQUEST_CODE.EDIT_EVENT) {
                Event event = (Event) data.getSerializableExtra("EeventResult");
                // Create a new event, add into events set
                events.addEvent(event, this);
                refreshEventListView();
            } else if(requestCode == REQUEST_CODE.CALENDAR_VIEW) {
                refreshEventListView();
            }
        } else if(resultCode == ResultCode.RESULT_DELETE) {     // Delete the event
            if(requestCode == REQUEST_CODE.EDIT_EVENT) {    // Check request code for security
                String eventId = (String) data.getStringExtra("EventId");
                events.deleteEvent(eventId, this);
                refreshEventListView();
            }
        } else {
            Log.e("MainActivity", "Create event activity return error");
        }
    }

    private void refreshEventListView() {
        adapter.clear();
        if(!isDescendOrder) {
            adapter.addAll(events.getAscendOrder());
        } else {
            adapter.addAll(events.getDescenderOrder());
        }
        adapter.notifyDataSetChanged();
    }

    public void deleteEvent(String eventId) {
        events.deleteEvent(eventId, this);
        adapter.clear();
        adapter.addAll(events.toList());
        adapter.notifyDataSetChanged();
    }

    public void editEvent(Event event) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra("Event", event);
        startActivityForResult(intent, REQUEST_CODE.EDIT_EVENT);
    }
}
