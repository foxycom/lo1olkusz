/*
 * Copyright 2016 Piotr Janczyk
 *
 * This file is part of lo1olkusz unofficial app.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pjanczyk.lo1olkusz.synchronization;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.pjanczyk.lo1olkusz.receivers.AlarmReceiver;
import com.pjanczyk.lo1olkusz.synchronization.SyncService;
import com.pjanczyk.lo1olkusz.utils.Settings;

public class SyncTimingHelper {

    private static final int SYNC_INTERVAL = 15 * 60 * 1000; // 15 minutes

    public static final int BOOT_COMPLETE = 1;
    public static final int ALARM_RECEIVE = 2;
    public static final int SETTINGS_CHANGED = 3;
    public static final int ACTIVITY_START = 4;
    public static final int USER_REQUEST = 5;

    public static void handleAction(Context context, int action) {
        Settings settings = new Settings(context);
        boolean periodicSync = settings.getLNNotify() || settings.getReplacementsNotify();

        if (periodicSync) {
            setAlarm(context);
        } else {
            if (action == SETTINGS_CHANGED) {
                cancelAlarm(context);
            }
        }

        if (periodicSync || action == ACTIVITY_START || action == USER_REQUEST) {
            startService(context);
        }
    }

    private static void startService(Context context) {
        Intent service = new Intent(context, SyncService.class);
        WakefulBroadcastReceiver.startWakefulService(context, service);
    }

    private static void setAlarm(Context context) {
        long alarmTime = SystemClock.elapsedRealtime() + SYNC_INTERVAL;

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTime, pendingIntent);
        Log.i("AlarmHelper", "Set alarm");
    }

    private static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
