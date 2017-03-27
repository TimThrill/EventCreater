package com.cong.eventcreater.UI;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cong.eventcreater.R;
import com.cong.eventcreater.UI.DailyEventViewDialog;
import com.cong.eventcreater.model.Event;
import com.cong.eventcreater.model.EventDetail;
import com.cong.eventcreater.model.Events;
import com.cong.eventcreater.model.REQUEST_CODE;
import com.cong.eventcreater.model.ResultCode;
import com.cong.eventcreater.model.UpdateEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btPreviousMonth;
    private ImageButton btNextMonth;
    private TextView calendarTitle;
    private GridLayout gridView;
    private TextView[][] weekTitle;
    private TextView[][] days;
    private String[] strWeekTitle = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"};
    private String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                            "JULY", "AUGUST", "SEPTEMBER", "OCTOBOR", "NOVEMBER", "DECEMBER"};
    private GregorianCalendar currentDate;
    private Events events;  // All events
    private HashMap<String, Event> eventsToday; // The events in current date
    private ArrayList<EventDetail> eventsDetailToday;
    private Intent i;
    private DailyEventViewDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        events = new Events();
        btPreviousMonth = (ImageButton)findViewById(R.id.previousMonth);
        btNextMonth = (ImageButton)findViewById(R.id.nextMonth);
        btPreviousMonth.setOnClickListener(this);
        btNextMonth.setOnClickListener(this);

        calendarTitle = (TextView)findViewById(R.id.calendarTitle);
        gridView = (GridLayout)findViewById(R.id.calendarGrid);
        weekTitle = new TextView[1][7];
        for(int row = 0; row < weekTitle.length; row++)
        {
            for(int columun = 0; columun < weekTitle[row].length; columun++) {
                weekTitle[row][columun] = new TextView(this);
                GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                param.width = 180;
                weekTitle[row][columun].setLayoutParams(param);
            }
        }
        days = new TextView[6][7];
        for(int i = 0; i < days.length; i++)
        {
            for(int j = 0; j < days[i].length; j++) {
                days[i][j] = new TextView(this);
                GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                param.width = 180;
                param.height = 160;
                days[i][j].setLayoutParams(param);
            }
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentDate = new GregorianCalendar(currentYear, currentMonth, currentDay);
        setUpCalendarView(currentYear, currentMonth, currentDay);
    }

    private boolean setUpCalendarView(int year, int month, int day) {
        gridView.removeAllViews();
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        date.set(Calendar.DAY_OF_MONTH, 1); // Set date to the first day of this month
        Calendar tmp = Calendar.getInstance();
        tmp.set(year, month, day);  // Initialise with input month

        calendarTitle.setText(months[date.get(Calendar.MONTH)] + " " + String.valueOf(date.get(Calendar.YEAR)));

        // Set first row which represents the week title
        for(int i = 0; i < 7; i++) {
            weekTitle[0][i].setText(strWeekTitle[i]);
            weekTitle[0][i].setBackgroundColor(Color.parseColor("#d1d1e0"));
            gridView.addView(weekTitle[0][i], i);
        }

        for(int i = 0; i < days.length; i++) {
            for(int j = 0; j < days[i].length; j++) {
                days[i][j].setBackgroundColor(Color.TRANSPARENT); // Reset background color
                if(j == (date.get(Calendar.DAY_OF_WEEK) - 1)) {   // Same week day, fill date into cell
                    HashMap<String, Event> result = hasEvent(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                    if(result.size() != 0) {
                        days[i][j].setBackgroundColor(Color.parseColor("#ffb3ff")); // Has event, set background color
                        days[i][j].setTag(result);
                        days[i][j].setOnClickListener(this);    // Set onclick listener
                    } else {
                        days[i][j].setBackgroundColor(Color.parseColor("#99ddff"));
                    }
                    days[i][j].setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
                    registerForContextMenu(days[i][j]);     // Register for contextual menu
                    if(date.get(Calendar.DAY_OF_MONTH) == tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        gridView.addView(days[i][j], (i + 1) * 7 + j);
                        return true;    // Reach the last day of month, finish draw the layout and return
                    } else {
                        date.add(Calendar.DATE, 1);
                    }
                } else {
                    days[i][j].setText("");
                    days[i][j].setBackgroundColor(Color.parseColor("#99ddff"));
                }
                gridView.addView(days[i][j], (i + 1) * 7 + j);
            }
        }
        return false;
    }

    /**
     *
     * @param year
     * @param month
     * @param day
     * @return  A set of events in this date
     */
    private HashMap<String, Event> hasEvent(int year, int month, int day) {
        HashMap<String, Event> eventsToday = new HashMap<String, Event>();
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        for(HashMap.Entry<String, Event> event : events.getEvents().entrySet()) {
            GregorianCalendar eventDate = event.getValue().getStartDate();
            if(((eventDate != null) && (eventDate.get(Calendar.YEAR) == date.get(Calendar.YEAR)))
                    && (eventDate.get(Calendar.MONTH) == date.get(Calendar.MONTH))
                    && (eventDate.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH))) {
                eventsToday.put(event.getKey(), event.getValue());
            }
        }
        return eventsToday;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.nextMonth) {
            currentDate.add(Calendar.MONTH, 1); // Move to next month
            // Refresh the grid view
            setUpCalendarView(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            return;
        } else if(id == R.id.previousMonth) {
            currentDate.add(Calendar.MONTH, -1);    // Move to previous month
            // Refresh the grid view
            setUpCalendarView(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            return;
        } else {
            eventsToday = (HashMap<String, Event>)v.getTag();
            eventsDetailToday = getEventsDetail(eventsToday);
            dialog = new DailyEventViewDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("EventsDetail", eventsDetailToday);
            dialog.setArguments(bundle);

            FragmentManager fm = getFragmentManager();
            dialog.show(fm, "View Event Detail");
        }
        Log.d("CalendarActivity", "Unknown click button");
    }

    /**
     *
     * @param eventsToday
     * @return A list of string that contains the details of events
     */
    private ArrayList<EventDetail> getEventsDetail(HashMap<String, Event> eventsToday) {
        // Reorder the events to ascend order
        List<Event> levents = events.getAscendOrder();
        ArrayList<EventDetail> eventsDetail = new ArrayList<EventDetail>();
        for(int i = 0; i < levents.size(); i++) {
                eventsDetail.add(new EventDetail(levents.get(i).getId(), levents.get(i).getEventDetail()));
        }
        return eventsDetail;
    }

    /**
     * When user click back button, return to main activity and return the events
     */
    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        setResult(RESULT_OK);
        finish();
    }

    public void editEvent(EventDetail event) {
        i = new Intent(this, CreateEventActivity.class);
        i.putExtra("Event", events.getEvent(event.getEventId()));
        startActivityForResult(i, REQUEST_CODE.EDIT_EVENT);
        dialog.dismiss();
    }

    public void deleteEvent(final EventDetail event) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CalendarActivity.this);
        alertDialogBuilder.setMessage("Are you sure to delete this event?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the event
                events.deleteEvent(event.getEventId(), CalendarActivity.this);
                (CalendarActivity.this).dialog.dismiss();   // Remove the dialog
                // Refresh the grid view
                setUpCalendarView(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE.EDIT_EVENT) {
            if(resultCode == RESULT_OK) {
                Event event = (Event)data.getSerializableExtra("EeventResult");
                // Create a new event, add into events set
                events.addEvent(event, this);
                // Refresh the grid view
                setUpCalendarView(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
                // Update event in database
                (new UpdateEvent(this)).execute(event);
            } else if(resultCode == ResultCode.RESULT_DELETE) {
                Event deleteEvent = (Event)i.getSerializableExtra("Event");
                events.deleteEvent(deleteEvent.getId(), this);
                // Refresh the grid view
                setUpCalendarView(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));
            }
        }
    }

    // Haven't implemented yet
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_edit_event, menu);
    }
}
