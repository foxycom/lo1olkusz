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

package com.pjanczyk.lo1olkusz.ui.firstrun;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.synchronization.Synchronizer;
import com.pjanczyk.lo1olkusz.utils.Settings;
import com.pjanczyk.lo1olkusz.utils.network.NetworkAvailability;

/**
 * The first screen of {@link FirstRunActivity}
 */
public class FirstRunWelcomeFragment extends Fragment {

    private FirstRunActivity activity;
    private ProgressBar progressBar;
    private Button buttonNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_run_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        buttonNext = (Button) view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onButtonNextClick();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        buttonNext.setVisibility(View.GONE);
        beginSync();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (FirstRunActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    private void beginSync() {
        if (!NetworkAvailability.isConnected(activity)) {
            showErrorMessage(getString(R.string.no_internet_connection));
            return;
        }

        Settings settings = new Settings(activity);
        settings.edit()
                .setApiTimestamp(0)
                .apply();

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void[] params) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                }

                Synchronizer synchronizer = new Synchronizer(activity);
                synchronizer.sync();

                return synchronizer.getErrorMessage();
            }

            @Override
            protected void onPostExecute(String error) {
                if (error != null) {
                    showErrorMessage(error);
                } else {
                    onSyncSuccess();
                }
            }
        };
        task.execute();
    }

    private void onSyncSuccess() {
        progressBar.setVisibility(View.GONE);
        buttonNext.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String error) {
        new AlertDialog.Builder(activity)
                .setMessage(error)
                .setNeutralButton(getResources().getString(R.string.close), null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                })
                .show();
    }

}
