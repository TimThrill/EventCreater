package com.cong.eventcreater.model;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.cong.eventcreater.R;

/**
 * Created by admin on 15/09/2016.
 */
public class EventNotification {
    private Context context;
    private Intent resultIntent;
    private PendingIntent resultPendingIntent;
    private NotificationCompat.Builder mBuilder;

    public EventNotification(Context context, Intent resultIntent, String title, String summary) {
        this.context = context;
        this.resultIntent = resultIntent;
        this.mBuilder =
                new NotificationCompat.Builder(this.context)
                .setContentTitle(title)
                .setContentText(summary)
                .setSmallIcon(R.drawable.ic_timer)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_timer))
                .setSound(
                    RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(Notification.PRIORITY_HIGH);
        getPendingIntent();
        mBuilder.setContentIntent(this.resultPendingIntent);
    }

    public void getPendingIntent() {
        this.resultPendingIntent =
                PendingIntent.getActivity(this.context,
                        0,
                        this.resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        return this.mBuilder;
    }
}
