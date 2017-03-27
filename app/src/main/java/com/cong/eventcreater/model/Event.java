package com.cong.eventcreater.model;

import java.io.Closeable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Cong MA on 20/07/2016.
 */
public class Event implements Serializable, Cloneable {
    private String title; // The title of the event
    private GregorianCalendar startDate; // The start date of the event
    private GregorianCalendar endDate; // The end date of the event
    private String venue; // The location of the event
    private double longitude; // The longitude of the event adress
    private double latitude; // The latitude of the event address
    private String note; // Additional information
    private HashMap<Integer, Person> attendees; // A list of individuals who are invited to this event
    private String id; // A randomly generated combination of numbers and letters, which uniquely identifies an event
    private String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN",
            "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private String[] strWeekTitle = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"};
    private String recordingFilePath;
    private int saftyTime;

    public Event(String id)
    {
        this.id = id;
        attendees = new HashMap<Integer, Person>();
        this.saftyTime = 15;
    }

    public Event(String id,
                 String title,
                 long startDate,
                 long endDate,
                 String venue,
                 double longitude,
                 double latitude,
                 String note,
                 Person attendee,
                 int saftyTime) {
        this.id = id;
        this.startDate = new GregorianCalendar();
        this.startDate.setTimeInMillis(startDate);
        this.endDate = new GregorianCalendar();
        this.endDate.setTimeInMillis(endDate);
        this.title = title;
        this.venue = venue;
        this.longitude = longitude;
        this.latitude = latitude;
        this.note = note;
        this.attendees = new HashMap<>();
        if(attendee != null) {
            attendees.put(attendee.getId(), attendee);
        }
        this.saftyTime = saftyTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GregorianCalendar getStartDate() {
        return startDate;
    }

    public void setStartDate(GregorianCalendar startDate) {
        this.startDate = startDate;
    }

    public GregorianCalendar getEndDate() {
        return endDate;
    }

    public boolean setEndDate(GregorianCalendar endDate) {
        // End date must be after start date
        if(this.startDate != null) {
            if (endDate.compareTo(this.startDate) > 0) {
                this.endDate = endDate;
                return true;
            }
        }
        return false;
    }

    public void setEndDateWithoutValidation(GregorianCalendar endDate) {
        this.endDate = endDate;
    }

    public void setEndDateWithoutValidation(int year, int month, int day) {
        if(this.endDate != null) {
            this.endDate.set(Calendar.YEAR, year);
            this.endDate.set(Calendar.MONTH, month);
            this.endDate.set(Calendar.DAY_OF_MONTH, day);
        }
    }

    public void setEndDateWithoutValidation(int hour, int minute) {
        if(this.endDate != null) {
            this.endDate.set(Calendar.HOUR_OF_DAY, hour);
            this.endDate.set(Calendar.MINUTE, minute);
        }
    }

    public boolean setEndDate(int year, int month, int day) {
        GregorianCalendar tmpEndDate;
        if(this.endDate != null) {
            tmpEndDate = new GregorianCalendar(year, month, day, this.endDate.get(Calendar.HOUR_OF_DAY), this.endDate.get(Calendar.MINUTE));
        } else {
            tmpEndDate = this.endDate;
            tmpEndDate.set(Calendar.YEAR, year);
            tmpEndDate.set(Calendar.MONTH, month);
            tmpEndDate.set(Calendar.DAY_OF_MONTH, day);
        }
        return this.setEndDate(tmpEndDate);
    }

    public boolean setEndDate(int hour, int minute) {
        GregorianCalendar tmpEndDate;
        if(this.endDate != null) {
            tmpEndDate = new GregorianCalendar(this.endDate.get(Calendar.YEAR),
                            this.endDate.get(Calendar.MONTH),
                            this.endDate.get(Calendar.DAY_OF_MONTH),
                            hour, minute);
        } else {
            tmpEndDate = new GregorianCalendar();
            tmpEndDate.set(Calendar.HOUR_OF_DAY, hour);
            tmpEndDate.set(Calendar.MINUTE, minute);
        }
        return this.setEndDate(tmpEndDate);
    }

    public void initEndDate(int hour, int minute) {
        GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute);
        this.endDate = date;
    }

    public String getVenue() {
        return this.venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String node) {
        this.note = node;
    }

    public HashMap<Integer, Person> getAttendees() {
        return attendees;
    }

    public void setAttendees(HashMap<Integer, Person> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(Person attendee) {
        if(attendees != null) {
            this.attendees.put(attendee.getId(), attendee);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String r = "Title: " + this.title + "\n"
                + "Start Date: "
                + formatDate(this.startDate)
                + "\n"
                + "End Date: "
                + formatDate(this.endDate)
                + "\n"
                + "Number of Attendees: "
                + attendees.size();
        return r;
    }

    private String formatDate(GregorianCalendar date) {
        String str = (date == null) ? "NULL" : (String.valueOf(date.get(Calendar.DAY_OF_MONTH))
                + "/"
                + months[date.get(Calendar.MONTH)]
                + "/"
                + date.get(Calendar.YEAR)
                + " "
                + strWeekTitle[date.get(Calendar.DAY_OF_WEEK) - 1]
                + " "
                + String.format("%02d", date.get(Calendar.HOUR_OF_DAY))
                + ":"
                + String.format("%02d", date.get(Calendar.MINUTE)));
        return str;
    }

    public boolean validateDate() {
        if((this.startDate != null) && (this.endDate != null)) {
            if(this.endDate.compareTo(this.startDate) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @return A string of attendess' name
     */
    public String getAttendeesName() {
        String names = "";
        for(HashMap.Entry<Integer, Person> attendee : attendees.entrySet()) {
            names += attendee.getValue().getFullName() + "\n";
        }
        return names;
    }

    public String getEventDetail() {
        String str = "Title: "
                + (title == null ? "" : title)
                + "\n"
                + "Start Time: "
                + (startDate == null ? "" : formatDate(startDate))
                + "\n"
                + "End Time: "
                + (endDate == null ? "" : formatDate(endDate))
                + "\n"
                + "Venue: "
                + (venue == null ? "" : venue)
                + "\n"
                + "Location: "
                + String.format("%.3f", latitude) + " " + String.format("%.3f", longitude)
                + "\n"
                + "Note: "
                + (note == null ? "" : note)
                + "\n"
                + "Attendees: "
                + getAttendeesName();
        return str;
    }

    public String getRecordingFilePath() {
        return recordingFilePath;
    }

    public void setRecordingFilePath(String recordingFilePath) {
        this.recordingFilePath = recordingFilePath;
    }

    public int getSaftyTime() {
        return saftyTime;
    }

    public void setSaftyTime(int saftyTime) {
        this.saftyTime = saftyTime;
    }
}
