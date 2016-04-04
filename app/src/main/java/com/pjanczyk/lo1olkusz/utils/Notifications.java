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

package com.pjanczyk.lo1olkusz.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.ui.main.MainActivity;

public class Notifications {

    public static void handleNotifications(Context context, News news) {
        Settings settings = new Settings(context);
        int userNumber = settings.getUserNumber();
        String userClass = settings.getUserClass();

        //show notifications
        if (settings.getLNNotify()) {
            for (LuckyNumber ln : news.luckyNumbers) {
                if (ln.getValue() == userNumber || ln.isEmpty()) {
                    handleUserLN(context, ln);
                }
            }
        }

        if (settings.getReplacementsNotify()) {
            for (Replacements repl : news.replacements) {
                if (repl.getClassName().equalsIgnoreCase(userClass)) {
                    handleUserReplacements(context, repl);
                }
            }
        }
    }

    public static void handleUserLN(Context context, LuckyNumber newLN) {
        int id = luckyNumberId(newLN);

        if (newLN.isEmpty()) {
            cancelNotification(context, id);
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_DATE, newLN.getDate().toString());

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntent(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String text = context.getString(R.string.notification_ln_text,
                newLN.getValue(),
                TimeFormatter.relativeDate(newLN.getDate()));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_lucky_number)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentTitle(context.getString(R.string.notification_ln_title))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        Settings settings = new Settings(context);
        if (settings.getLNNotifySound()) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (settings.getLNNotifyVibration()) {
            builder.setVibrate(new long[]{0, 50, 50, 100, 25, 200});
        }

        showNotification(context, id, builder.build());
    }

    public static void handleUserReplacements(Context context,
                                              Replacements newReplacements) {
        int id = replacementsId(newReplacements);

        if (newReplacements.isEmpty()) {
            cancelNotification(context, id);
            return;
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_DATE, newReplacements.getDate().toString());
        intent.putExtra(MainActivity.EXTRA_CLASS, newReplacements.getClassName());

        PendingIntent pendingIntent = TaskStackBuilder.create(context)
                .addParentStack(MainActivity.class)
                .addNextIntent(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String text = context.getString(R.string.notification_replacements_text,
                newReplacements.getClassName(),
                TimeFormatter.relativeDate(newReplacements.getDate()));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_replacements)
                .setColor(ContextCompat.getColor(context, R.color.primary))
                .setContentTitle(context.getString(R.string.notification_replacements_title))
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Settings settings = new Settings(context);
        if (settings.getReplacementsNotifySound()) {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        }
        if (settings.getReplacementsVibration()) {
            builder.setVibrate(new long[]{0, 100, 100, 100});
        }

        showNotification(context, id, builder.build());
    }

    private static int replacementsId(Replacements r) {
        int result = r.getClassName().hashCode();
        result = 31 * result + r.getDate().hashCode();
        result = 2 * result;
        return result;
    }

    private static int luckyNumberId(LuckyNumber n) {
        int result = n.getDate().hashCode();
        result = 2 * result + 1;
        return result;
    }

    private static void cancelNotification(Context context, int id) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(id);
    }

    private static void showNotification(Context context, int id, Notification notification) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notification);
    }
}
