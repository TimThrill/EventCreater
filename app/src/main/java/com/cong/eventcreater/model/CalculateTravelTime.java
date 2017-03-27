package com.cong.eventcreater.model;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.JsonReader;
import android.util.Log;

import com.cong.eventcreater.UI.CreateEventActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by admin on 22/09/2016.
 */

public class CalculateTravelTime extends AsyncTask<Location, Void, Void> {
    private final String TAG = "CalculateTravelTime";
    private Events events;
    private Context context;

    public CalculateTravelTime(Context context) {
        this.context = context;
        this.events = new Events();
    }

    @Override
    protected Void doInBackground(Location... params) {
        Location curLocation = params[0];
        calTravelTime(curLocation);
        return null;
    }

    private void calTravelTime(Location curLocation) {

        for(Map.Entry<String, Event> event : events.getEvents().entrySet()) {
            try {
                int duration = googleDistanceMatrix(curLocation, event.getValue().getLatitude(), event.getValue().getLongitude()) + event.getValue().getSaftyTime() * 60;
                GregorianCalendar now = new GregorianCalendar();
                long spareTime = (event.getValue().getStartDate().getTimeInMillis() - now.getTimeInMillis()) / 1000; // Get the time from now to the time that event starts, in seconds
                if((spareTime >= 0) && (spareTime - duration) < 0) {  // We don't have enough time now, push a notification to the user
                    Log.d(TAG, "Do not have enough time");
                    onFireNotification(event.getValue());
                } else {
                    Log.d(TAG, "Take easy, you have plenty time");
                }
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    private int googleDistanceMatrix(Location curLocation, double desLatitude, double desLongitude) throws IOException {
        int duration = 0;
        String strUrl = "https://maps.googleapis.com/maps/api/distancematrix/json"
                + "?"
                // + "origins=" + curLocation.getLatitude() + "," + curLocation.getLongitude()
                + "origins=" + "-37.865390" + "," + "144.976750"
                + "&"
                + "destinations=" + desLatitude + "," + desLongitude
                + "&"
                + "key=" + "AIzaSyDaXnEfiQA28ADywpefH-UE0x_zWaUyrnQ";
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            Log.d(TAG, "GET Request: " + url);
            urlConnection = (HttpsURLConnection)url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            ParseDistanceMatrixResponse parse = new ParseDistanceMatrixResponse();
            parse.readJsonStream(in);
            if(parse.getDistanceMatrixResponse().getTopStatus().equals("OK")
                    && parse.getDistanceMatrixResponse().getElementStatus().equals("OK")) {
                duration = parse.getDistanceMatrixResponse().getDuration();
                Log.d(TAG, "Response duration: " + duration);
            } else {
                Log.d(TAG, "Parse distance matrix response failed");
            }
        } catch (MalformedURLException e) {

        } finally {
            urlConnection.disconnect();
        }
        return duration;
    }

    private void onFireNotification(Event event) {
        EventNotification eventNotification =
                new EventNotification(context,
                        new Intent(context, CreateEventActivity.class)
                                .putExtra("Event", event),
                        event.getTitle(),
                        event.getEventDetail());
        // Sets an ID for the notification
        int mNotificationId = event.getId().hashCode();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, eventNotification.getNotificationBuilder().build());
    }
}
