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

import android.content.Context;
import android.util.Log;

import com.pjanczyk.lo1olkusz.BuildConfig;
import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.storage.SavedBells;
import com.pjanczyk.lo1olkusz.storage.SavedLuckyNumbers;
import com.pjanczyk.lo1olkusz.storage.SavedReplacements;
import com.pjanczyk.lo1olkusz.storage.SavedTimetables;
import com.pjanczyk.lo1olkusz.utils.AppVersion;
import com.pjanczyk.lo1olkusz.utils.Notifications;
import com.pjanczyk.lo1olkusz.utils.Settings;
import com.pjanczyk.lo1olkusz.utils.network.BadResponseException;

import org.joda.time.LocalDate;

import java.io.IOException;

public class Synchronizer {

    private static final String TAG = "Synchronizer";

    private final Context context;
    private News news;
    private String errorMsg;

    public Synchronizer(Context context) {
        this.context = context;
    }

    public News getNews() {
        return news;
    }

    public String getErrorMessage() {
        return errorMsg;
    }

    /**
     * Fetches news, updates the local storage and shows notifications
     * @return True on success
     */
    public boolean sync() {

        try {
            fetchNews();
        } catch (IOException e) {
            errorMsg = context.getString(R.string.sync_error);
            return false;
        } catch (BadResponseException e) {
            errorMsg = context.getString(R.string.sync_error_bad_response);
            return false;
        }
        updateLocalStorage();
        Notifications.handleNotifications(context, news);

        return true;
    }

    private void fetchNews()
            throws IOException, BadResponseException {

        Settings settings = new Settings(context);

        int timestamp = settings.getApiTimestamp();
        int version = AppVersion.getVersionCode(context);
        if (BuildConfig.DEBUG) {
            version += 100000;
        }

        String androidId = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        Log.d(TAG, "Getting news with androidId=" + androidId + ",version=" + version + ",timestamp=" + timestamp);

        try {
            news = Api.getNews(androidId, version, timestamp);
            Log.d(TAG, "Got news, new timestamp=" + news.timestamp);
        } catch (IOException | BadResponseException e) {
            Log.e(TAG, "Error occurred", e);
            throw e;
        }
    }

    private void updateLocalStorage() {
        SavedTimetables savedTimetables = new SavedTimetables(context);
        SavedReplacements savedReplacements = new SavedReplacements(context);
        SavedLuckyNumbers savedLuckyNumbers = new SavedLuckyNumbers(context);
        SavedBells savedBells = new SavedBells(context);

        Settings settings = new Settings(context);
        settings.edit();

        LocalDate lastCleanUp = settings.getLastCleanUp();
        if (lastCleanUp == null || lastCleanUp.isBefore(LocalDate.now())) {
            Log.d(TAG, "Cleaning up");
            savedReplacements.cleanUp();
            savedLuckyNumbers.cleanUp();

            settings.setLastCleanUp(LocalDate.now());
        }

        if (news.bells != null) {
            savedBells.save(news.bells);
        }
        for (Timetable t : news.timetables) {
            savedTimetables.save(t);
        }
        for (Replacements r : news.replacements) {
            savedReplacements.save(r);
        }
        for (LuckyNumber ln : news.luckyNumbers) {
            savedLuckyNumbers.save(ln);
        }

        settings.setLastSyncTime(System.currentTimeMillis());
        settings.setApiTimestamp(news.timestamp);

        settings.apply();

        Log.d(TAG, "Updated local storage");
    }

}