package com.cong.eventcreater.model;

import android.content.Context;

import com.cong.eventcreater.model.DeleteEvent;
import com.cong.eventcreater.model.Event;
import com.cong.eventcreater.model.InsertEvent;
import com.cong.eventcreater.model.LoadAllEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 21/09/2016.
 */

public class Events {
    private static HashMap<String, Event> events = new HashMap<>();


    public HashMap<String, Event> getEvents() {
        return events;
    }

    public void loadAllEvents(Context context) {
        // Load all events from database
        this.events = new LoadAllEvents(context).retrieveAllEvents();
    }

    public void addEvent(Event event, Context context) {
        if(events != null) {
            events.put(event.getId(), event);
            // Add event to database
            (new InsertEvent(context)).execute(event);
        }
    }

    public void deleteEvent(String eventId, Context context) {
        if(events.containsKey(eventId)) {
            // Delete event from database
            (new DeleteEvent(context)).execute(events.get(eventId));
            events.remove(eventId);
        }
    }

    public List<Event> toList()
    {
        List<Event> listEvents = new ArrayList<Event>();
        for(Map.Entry<String, Event> event : events.entrySet()) {
            listEvents.add(event.getValue());
        }
        return listEvents;
    }

    public List<Event> getAscendOrder() {
        List<Event> lEvents = toList();
        Collections.sort(lEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if((lhs.getStartDate() != null) && (rhs.getStartDate() != null)) {
                    return lhs.getStartDate().compareTo(rhs.getStartDate());
                } else if(lhs.getStartDate() == null) {
                    return 1;
                } else if(rhs.getStartDate() == null) {
                    return -1;
                }
                return 0;
            }
        });
        return lEvents;
    }

    public List<Event> getDescenderOrder() {
        List<Event> lEvents = toList();
        Collections.sort(lEvents, new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                if((lhs.getStartDate() != null) && (rhs.getStartDate() != null)) {
                    return rhs.getStartDate().compareTo(lhs.getStartDate());
                } else if(rhs.getStartDate() == null) {
                    return 1;
                } else if(lhs.getStartDate() == null) {
                    return -1;
                }
                return 0;
            }
        });
        return lEvents;
    }

    public Event getEvent(String eventId) {
        if(events.containsKey(eventId)) {
            return events.get(eventId);
        }
        return null;
    }
}
