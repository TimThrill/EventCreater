package com.cong.eventcreater.model;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AlarmManagerService extends IntentService {
    private final String TAG = "AlarmManagerService";

    public AlarmManagerService() {
        super("AlarmManagerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG, "On handle alarm manager service");
            AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(this, EventNotificationReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, i, 0);
            int alarmTimer = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("alarm_timer_list", "5")) * 60; // In seconds
            Log.d(TAG, "Alarm timer: " + alarmTimer);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, alarmTimer * 1000, alarmIntent);
        } else {
            Log.e(TAG, "Alarm manager service intent is null");
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AlarmManagerService Destroyed");
    }
}
