package com.example.socialreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CallCreateNotification extends BroadcastReceiver
{
    public static String INTENTID = "intentID";
    private static String title = "Social Reminder";
    private static String body;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        int id = intent.getIntExtra(INTENTID, 2);

        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1)
            body = "A new selection has been made.";
        else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 4)
            body = "You have 3 days left to talk to your current selection.";
        else
            body = "Wrong Day";

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, buildNotification(context, id));
    }

    public Notification buildNotification(Context context, int id)
    {
        if(id == 2) body += " | intent intExtra could not be read";

        Intent intent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(id,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "mChannel")
                .setSmallIcon(android.R.drawable.btn_star_big_on)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return  builder.build();
    }
}
