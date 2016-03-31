package com.jon.buzz;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

public class BackgroundCountdown extends IntentService {

    private NotificationManager mNotificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public BackgroundCountdown() {
        super("BackgroundCountdown Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int seconds = intent.getIntExtra("Seconds",0);

        // Tell notification manager to notify user
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Set stack as pending intent for when the notification is clicked
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        final Notification.Builder notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Buzz")
                .setContentText(seconds + " second timer is running")
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setColor(getResources().getColor(R.color.colorPrimary, null))
                .setAutoCancel(false)
                .setOngoing(true);

        startForeground(2,notification.build());

        for (int i = 0; i < seconds; i++) {
            try {
                System.out.println(i);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stopForeground(true);

        final Notification.Builder endNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Buzz")
                .setContentText(seconds + " second timer is complete")
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setColor(getResources().getColor(R.color.colorPrimary, null))
                .setAutoCancel(true);

        mNotificationManager.notify(1, endNotification.build());



    }

}
