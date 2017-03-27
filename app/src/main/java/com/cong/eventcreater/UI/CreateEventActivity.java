package com.cong.eventcreater.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cong.eventcreater.R;
import com.cong.eventcreater.model.Event;
import com.cong.eventcreater.model.Person;
import com.cong.eventcreater.model.REQUEST_CODE;
import com.cong.eventcreater.model.ResultCode;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnClickListener, OnMapReadyCallback {
    private final String TAG = "CreateEventActivity";
    private Button  btSelectStartDate, btSelectStartTime,
                    btSelectEndDate, btSelectEndTime, btCreateEvent, btCancelCreateEvent,
                    btRecording, btPlayRecording;
    private ImageButton btLoadContact;
    private TextView tvEventStartDate, tvEventStartTime, tvEventEndDate, tvEventEndTime;
    private EditText etEventName, etEventVenue, etLatitude, etLongitude, etNote, etSaftyTime;
    private LinearLayout contactsList;
    private Event event;
    private boolean selectStartDate;
    private MapFragment mapFragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Sets the toolbar as the app bar for the activity
        Toolbar myToolbar = (Toolbar) findViewById(R.id.create_event_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);    // Hide activity title in toolbar
        event = (Event)getIntent().getSerializableExtra("Event");

        // Get map fragment
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.d("Event id: ", event.getId());

        btLoadContact = (ImageButton)findViewById(R.id.buttonLoadAttendees);
        btSelectStartDate = (Button)findViewById(R.id.selectStartDate);
        btSelectStartTime = (Button)findViewById(R.id.selectStartTime);

        // Set current date and time shown on the button
        Calendar curTime = Calendar.getInstance();
        btSelectStartDate.setText((new SimpleDateFormat("dd/MM/yyyy").format(curTime.getTime())).toString());
        btSelectStartTime.setText((new SimpleDateFormat("hh:mm:ss").format(curTime.getTime())).toString());

        btSelectEndDate = (Button)findViewById(R.id.selectEndDate);
        btSelectEndTime = (Button)findViewById(R.id.selectEndTime);
        btCreateEvent = (Button)findViewById(R.id.create_event_ok);
        btCancelCreateEvent = (Button)findViewById(R.id.create_event_cancel);
        btRecording = (Button)findViewById(R.id.recording);
        btPlayRecording = (Button)findViewById(R.id.playRecording);
        btLoadContact.setOnClickListener(this);
        btSelectStartDate.setOnClickListener(this);
        btSelectStartTime.setOnClickListener(this);
        btSelectEndDate.setOnClickListener(this);
        btSelectEndTime.setOnClickListener(this);
        btCreateEvent.setOnClickListener(this);
        btCancelCreateEvent.setOnClickListener(this);
        btRecording.setOnClickListener(this);
        btPlayRecording.setOnClickListener(this);

        tvEventStartDate = (TextView)findViewById(R.id.eventStartDate);
        tvEventStartTime = (TextView)findViewById(R.id.eventStartTime);
        tvEventEndDate = (TextView)findViewById(R.id.eventEndDate);
        tvEventEndTime = (TextView)findViewById(R.id.eventEndTime);

        etEventName = (EditText)findViewById(R.id.inputEventName);
        etEventVenue = (EditText)findViewById(R.id.inputVenue);
        etLatitude = (EditText)findViewById(R.id.inputLatitude);
        etLongitude = (EditText)findViewById(R.id.inputLongitude);
        etNote = (EditText)findViewById(R.id.inputNote);
        etSaftyTime = (EditText)findViewById(R.id.inputSaftyTime);
        etEventName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    v.clearFocus(); // Remove the focus on edit text view

                    // Get input text and set event title
                    CreateEventActivity.this.getEvent().setTitle(etEventName.getText().toString());
                    return true;
                }
                return false;
            }
        });
        etEventVenue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    v.clearFocus(); // Remove the focus on edit text view

                    // Get input text and set event title
                    CreateEventActivity.this.getEvent().setVenue(etEventVenue.getText().toString());
                    return true;
                }
                return false;
            }
        });
        etLatitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    v.clearFocus(); // Remove the focus on edit text view

                    // Get input text and set event title
                    try {
                        CreateEventActivity.this.getEvent().setLatitude(Double.valueOf(etLatitude.getText().toString()));
                        Log.d(TAG, "latitude: " + Double.valueOf(etLatitude.getText().toString()));
                        mapFragment.getMapAsync(CreateEventActivity.this);  // Refresh the map
                    } catch (NumberFormatException e) {
                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);

//                      2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("Invalid format of latitude")
                                .setTitle("Error");

                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

//                      3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    return true;
                }
                return false;
            }
        });
        etLongitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    v.clearFocus(); // Remove the focus on edit text view

                    // Get input text and set event title
                    try {
                        CreateEventActivity.this.getEvent().setLongitude(Double.valueOf(etLongitude.getText().toString()));
                        Log.d(TAG, "longitude: " + Double.valueOf(etLongitude.getText().toString()));
                        mapFragment.getMapAsync(CreateEventActivity.this);  // Refresh the map
                    } catch (NumberFormatException e) {
                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);

//                      2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("Invalid format of longitude")
                                .setTitle("Error");

                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

//                      3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    return true;
                }
                return false;
            }
        });
        etNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    // Set node
                    CreateEventActivity.this.getEvent().setNote(etNote.getText().toString());
                    // Hide the keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    v.clearFocus(); // Remove the focus on edit text view
                }
            }
        });
        etSaftyTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard after input
                    InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    v.clearFocus(); // Remove the focus on edit text view

                    // Get input text and set event title
                    try {
                        CreateEventActivity.this.getEvent().setSaftyTime(Integer.valueOf(etSaftyTime.getText().toString()));
                        Log.d(TAG, "Safty time: " + Integer.valueOf(etSaftyTime.getText().toString()));
                        mapFragment.getMapAsync(CreateEventActivity.this);  // Refresh the map
                    } catch (NumberFormatException e) {
                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);

//                      2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("Invalid format of longitude")
                                .setTitle("Error");

                        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

//                      3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    return true;
                }
                return false;
            }
        });

        contactsList = (LinearLayout)findViewById(R.id.contact_list);

        initEventView(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            confirmDeleteEvent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDeleteEvent() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure to delete this event?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent();
                i.putExtra("EventId", CreateEventActivity.this.event.getId());
                setResult(ResultCode.RESULT_DELETE, i);
                finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE.LOAD_CONTACTS)
        {
            if(resultCode == RESULT_OK) {
                event.setAttendees((HashMap<Integer, Person>)data.getSerializableExtra("Attendees_Result"));
                setAttendeesView(event.getAttendees()); // Populdate attendees view
            }
            else
            {
                Log.e("LoadContactActivity", "Load failed");
            }
        } else if(requestCode == REQUEST_CODE.RECORDING) {
            if(resultCode == RESULT_OK) {
                String fileName = data.getStringExtra("recordingFile");
                event.setRecordingFilePath(fileName);
            }
        } else {
            Log.d("CreateEventActivity", "Unknown returned activity: " + requestCode);
        }
    }

    @Override
    public void onClick(View v) {
        int btId = v.getId();
        if(btId == R.id.buttonLoadAttendees) {
            Intent i = new Intent(CreateEventActivity.this, LoadContactsActivity.class);
            i.putExtra("Attendees", event.getAttendees());
            if(getParent() == null) {
                startActivityForResult(i, REQUEST_CODE.LOAD_CONTACTS);
            } else {
                getParent().startActivityForResult(i, REQUEST_CODE.LOAD_CONTACTS);
            }
        } else if(btId == R.id.selectStartDate) {
            selectStartDate = true;
            showDatePickerDialog();
        } else if(btId == R.id.selectStartTime) {
            selectStartDate = true;
            showTimePickerDialog();
        } else if(btId == R.id.selectEndDate) {
            selectStartDate = false;
            showDatePickerDialog();
        } else if(btId == R.id.selectEndTime) {
            selectStartDate = false;
            showTimePickerDialog();
        } else if(btId == R.id.create_event_ok) {
            if(event.validateDate()) {
                Intent i = new Intent();
                i.putExtra("EeventResult", this.event);
                if (getParent() == null) {
                    setResult(RESULT_OK, i);
                    finish();
                } else {
                    getParent().setResult(RESULT_OK, i);
                    getParent().finish();
                }
            } else {
                showInvalidEndDateDialog();
            }
        } else if(btId == R.id.create_event_cancel) {
            finish();   // Finish activity
        } else if(btId == R.id.recording) {
            Intent intent = new Intent(CreateEventActivity.this, AudioRecordingActivity.class);
            intent.putExtra("filename", event.getId().replace("-", "_"));
            startActivityForResult(intent, REQUEST_CODE.RECORDING);
        } else if(btId == R.id.playRecording) {
            AudioRecordingActivity instance = new AudioRecordingActivity();
            instance.setPlayingFileName(event.getRecordingFilePath());
            try {
                instance.onPlay(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("CreateEventActivity", "Unknown button click: " + btId);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(selectStartDate) {
            setStartDateView(year, monthOfYear, dayOfMonth);
            // Set event start date
            if(event.getStartDate() == null) {
                event.setStartDate(new GregorianCalendar(year, monthOfYear, dayOfMonth));
            } else {
                event.getStartDate().set(year, monthOfYear, dayOfMonth);
            }
        } else {
            if(event.getEndDate() == null) {    // First time set end date
                event.setEndDateWithoutValidation(new GregorianCalendar(year, monthOfYear, dayOfMonth));
                setEndDateView(year, monthOfYear, dayOfMonth);
            } else {
                // Set event end date
                event.setEndDateWithoutValidation(year, monthOfYear, dayOfMonth); // End date is not after start date
                setEndDateView(year, monthOfYear, dayOfMonth);
            }
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(selectStartDate) {
            // Show start time
            setStartTimeView(hourOfDay, minute);

            if(event.getStartDate() == null) {
                event.setStartDate(new GregorianCalendar());
            }

            event.getStartDate().set(Calendar.HOUR_OF_DAY, hourOfDay);
            event.getStartDate().set(Calendar.MINUTE, minute);
        } else {        // Set start time of event end day
            if(event.getEndDate() != null) {
                if(event.getStartDate() != null) {  // Has start date. Compare end date and start date
                    if (!event.setEndDate(hourOfDay, minute)) {
                        showInvalidEndDateDialog();
                    } else {
                        setEndTimeView(hourOfDay, minute);
                    }
                } else {
                    setEndTimeView(hourOfDay, minute);
                }
            } else {    // Initial end date
                event.initEndDate(hourOfDay, minute);
                setEndTimeView(hourOfDay, minute);
            }
        }
    }

    public Event getEvent() {
        return event;
    }

    public boolean getSelectStartDate() {
        return selectStartDate;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Double latitude = event.getLatitude();
        Double longitude = event.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Marker");
        googleMap.clear();
        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(cameraUpdate);
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year;
            int month;
            int day;
            CreateEventActivity activity = (CreateEventActivity)getActivity();
            if(activity.getSelectStartDate()) {     // Set start date
                if(activity.getEvent().getStartDate() == null) {
                    // Use the current date as the default date in the picker
                    final Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    Calendar date = activity.getEvent().getStartDate();
                    year = date.get(Calendar.YEAR);
                    month = date.get(Calendar.MONTH);
                    day = date.get(Calendar.DAY_OF_MONTH);
                }
            } else {        // Set end date
                if(activity.getEvent().getEndDate() == null) {
                    // Use the current date as the default date in the picker
                    final Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    Calendar date = activity.getEvent().getEndDate();
                    year = date.get(Calendar.YEAR);
                    month = date.get(Calendar.MONTH);
                    day = date.get(Calendar.DAY_OF_MONTH);
                }
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (CreateEventActivity)getActivity(), year, month, day);
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            CreateEventActivity activity = (CreateEventActivity)getActivity();
            int hour, minute;
            if(activity.getSelectStartDate()) { // Set event start time
                if(activity.getEvent().getStartDate() == null) {    // Event has no time
                    // Use the current time as the default values for the picker
                    final Calendar c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                } else {  // Event has a schedule
                    hour = activity.getEvent().getStartDate().get(Calendar.HOUR_OF_DAY);
                    minute = activity.getEvent().getStartDate().get(Calendar.MINUTE);
                }
            } else {        // Set event end time
                if(activity.getEvent().getEndDate() == null) {
                    // Use the current time as the default values for the picker
                    final Calendar c = Calendar.getInstance();
                    hour = c.get(Calendar.HOUR_OF_DAY);
                    minute = c.get(Calendar.MINUTE);
                } else {
                    hour = activity.getEvent().getEndDate().get(Calendar.HOUR_OF_DAY);
                    minute = activity.getEvent().getEndDate().get(Calendar.MINUTE);
                }
            }

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), (CreateEventActivity)getActivity(), hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }

    private void showInvalidEndDateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("The start date or end date is invalid.\n Please reselect your event date.");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void initEventView(Event event) {
        setEventTitleView(event.getTitle());    // Populate event title view
        setDateTimeView(event.getStartDate(), event.getEndDate());  // Populate start date and end date
        setAttendeesView(event.getAttendees()); // Populdate attendees view
        setVenueView(event.getVenue());
        setLocationView(event.getLatitude(), event.getLongitude());
        setNoteView(event.getNote());
        setSafetyTime(event.getSaftyTime());
    }

    private void setNoteView(String note) {
        if(note != null) {
            etNote.setText(note);
        }
    }

    private void setLocationView(double latitude, double longitude) {
        etLatitude.setText(String.format("%.3f", latitude));
        etLongitude.setText(String.format("%.3f", longitude));
    }

    private void setEventTitleView(String title) {
        if(title != null) {
            etEventName.setText(title);
        }
    }

    private void setDateTimeView(GregorianCalendar startDate, GregorianCalendar endDate) {
        if(startDate != null) {
            int year = startDate.get(Calendar.YEAR);
            int month = startDate.get(Calendar.MONTH);
            int day = startDate.get(Calendar.DAY_OF_MONTH);
            int hour = startDate.get(Calendar.HOUR_OF_DAY);
            int minute = startDate.get(Calendar.MINUTE);
            setStartDateView(year, month, day);
            setStartTimeView(hour, minute);
        }

        if(endDate != null) {
            int year = endDate.get(Calendar.YEAR);
            int month = endDate.get(Calendar.MONTH);
            int day = endDate.get(Calendar.DAY_OF_MONTH);
            int hour = endDate.get(Calendar.HOUR_OF_DAY);
            int minute = endDate.get(Calendar.MINUTE);
            setEndDateView(year, month, day);
            setEndTimeView(hour, minute);
        }
    }

    private void setStartDateView(int year, int monthOfYear, int dayOfMonth) {
        // Show start date
        tvEventStartDate.setText(String.valueOf(dayOfMonth)
                + "/"
                + String.valueOf(monthOfYear + 1)   // Horrible defination in Java, January is 0...
                + "/"
                + String.valueOf(year));
    }

    private void setEndDateView(int year, int monthOfYear, int dayOfMonth) {
        tvEventEndDate.setText(String.valueOf(dayOfMonth)
                + "/"
                + String.valueOf(monthOfYear + 1)
                + "/"
                + String.valueOf(year));
    }

    private void setStartTimeView(int hourOfDay, int minute) {
        tvEventStartTime.setText(String.format("%02d", hourOfDay)
                + ":"
                + String.format("%02d", minute));
    }

    private void setEndTimeView(int hourOfDay, int minute) {
        tvEventEndTime.setText(String.format("%02d", hourOfDay)
                + ":"
                + String.format("%02d", minute));
    }

    private void setAttendeesView(HashMap<Integer, Person> attendees) {
        // Clear contacts list every time get return from load contacts activity
        contactsList.removeAllViews();

        for (Map.Entry<Integer, Person> attendee : attendees.entrySet()) {
            Log.d("RESULT", "Full name: " + attendee.getValue().getFullName() + " " + attendee.getValue().getId());
            TextView contact = new TextView(this);  // Create a new text view for storing attendees' names
            contact.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contact.setText(attendee.getValue().getFullName()); // Set attendees' names to the text view
            contactsList.addView(contact);  // Add text view to current list
        }
    }

    private void setVenueView(String venue) {
        if(venue != null) {
            etEventVenue.setText(venue);
        }
    }

    private void setSafetyTime(int safetyTime) {
        etSaftyTime.setText(String.valueOf(safetyTime));
    }

    private void validationCheck() {
        return;
    }
}
