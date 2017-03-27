package com.cong.eventcreater.model;

import java.io.Serializable;

/**
 * Created by admin on 26/08/2016.
 */
public class EventDetail implements Serializable {
    String eventId;
    String eventDetail;
    public EventDetail(String eventId, String eventDetail) {
        this.eventId = eventId;
        this.eventDetail = eventDetail;
    }
    @Override
    public String toString() {
        return eventDetail;
    }

    public String getEventId() {
        return eventId;
    }
}

