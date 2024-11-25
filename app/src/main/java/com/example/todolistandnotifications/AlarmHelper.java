package com.example.todolistandnotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class AlarmHelper {

    public static void setAlarm(Context context, long triggerAtMillis, String title, String description) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, title.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                    } else {
                        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        context.startActivity(settingsIntent);
                    }
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setRepeatingAlarm(Context context, long triggerAtMillis, long intervalMillis, String title, String description) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, title.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        Toast.makeText(context, "Repeat " + triggerAtMillis + "-" + System.currentTimeMillis() + " " + intervalMillis, Toast.LENGTH_LONG).show();
        System.out.println("\n\nRepeat " + triggerAtMillis + "-" + System.currentTimeMillis() + " " + intervalMillis+"\n\n");
        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API level 31
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
                    } else {
                        Intent settingsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        context.startActivity(settingsIntent);
                    }
                } else {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                // Обработка ошибки
            }
        }
    }

    public static void cancelAlarm(Context context, String title) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, title.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
