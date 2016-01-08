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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.utils.network.NetworkAvailability;

public class SyncService extends Service {

    private final static String TAG = "SyncService";

    private final IBinder binder = new Binder();
    private SyncListener statusListener = null;
    private boolean taskRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!NetworkAvailability.isConnected(getApplicationContext())) {
            Log.i(TAG, "Network not available, sync request omitted");
            stopSelf();
            return START_NOT_STICKY;
        }

        if (taskRunning) {
            Log.i(TAG, "Task is already run, sync request omitted");
            stopSelf();
            return START_NOT_STICKY;
        }

        new Task(intent).execute();
        return START_NOT_STICKY;
    }

    public void setStatusListener(@Nullable SyncListener listener) {
        this.statusListener = listener;
    }

    public boolean isWorking() {
        return taskRunning;
    }

    public class Binder extends android.os.Binder {
        public SyncService getService() {
            // Return this instance of SyncService so clients can call public methods
            return SyncService.this;
        }
    }

    public interface SyncListener {
        void onSyncBegin();
        void onSyncSuccess(News news);
        void onSyncError(String errorMsg);
    }

    private class Task extends AsyncTask<Void, Void, Void> {

        private final Intent startIntent;
        private Synchronizer synchronizer;

        public Task(Intent startIntent) {
            this.startIntent = startIntent;
        }

        @Override
        protected void onPreExecute() {
            taskRunning = true;
            Log.i(TAG, "Starting sync task...");

            if (statusListener != null) {
                statusListener.onSyncBegin();
            }
        }

        @Override
        protected Void doInBackground(Void... unused0) {
            Context context = getApplicationContext();

            synchronizer = new Synchronizer(context);
            synchronizer.sync();

            return null;
        }

        @Override
        protected void onPostExecute(Void unused0) {
            taskRunning = false;

            if (statusListener != null) {
                if (synchronizer.getErrorMessage() == null) {
                    statusListener.onSyncSuccess(synchronizer.getNews());
                } else {
                    statusListener.onSyncError(synchronizer.getErrorMessage());
                }
            }

            SyncService.this.stopSelf(); //stops service unless it is bound
            WakefulBroadcastReceiver.completeWakefulIntent(startIntent);
            Log.i(TAG, "Sync task completed");
        }
    }
}
