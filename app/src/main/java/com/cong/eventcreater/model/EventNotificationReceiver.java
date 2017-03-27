package com.cong.eventcreater.model;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.NOTIFICATION_SERVICE;

public class EventNotificationReceiver extends BroadcastReceiver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private final String TAG = "EventNotificationReceiver";
    private Context context;
    GoogleApiClient googleApiClient;
    public EventNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d(TAG, "On receive event notification service");

        // Check internet connctivity
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = ((activeNetwork != null) &&
                activeNetwork.isConnectedOrConnecting());
        if(isConnected) {
            Log.d(TAG, "Network is availble");
            googleApiClient = new GoogleApiClient.Builder(context)
                                                .addConnectionCallbacks(this)
                                                .addOnConnectionFailedListener(this)
                                                .addApi(LocationServices.API)
                                                .build();
            googleApiClient.connect();
        } else {
            showNetworkErrorDialog();
            Log.d(TAG, "Network is not availble");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(lastLocation != null) {
            // Set for assignment test
            //lastLocation.setLatitude(-37.865390);
            //lastLocation.setLongitude(144.976750);
            Log.d(TAG, "Latitude: " + lastLocation.getLatitude() + " Longitude: " + lastLocation.getLongitude());
            (new CalculateTravelTime(context)).execute(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void showNetworkErrorDialog() {
        String text = "Network is diabled";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
