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

package com.pjanczyk.lo1olkusz.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.view.MenuItem;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.storage.SavedTimetables;
import com.pjanczyk.lo1olkusz.utils.AppVersion;
import com.pjanczyk.lo1olkusz.utils.Settings;
import com.pjanczyk.lo1olkusz.utils.settings.AppCompatPreferenceActivity;
import com.pjanczyk.lo1olkusz.utils.settings.MultiSelectListPreference;

import java.util.Collection;

public class SettingsActivity extends AppCompatPreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Settings settings;

    private SavedTimetables savedTimetables;

    private EditTextPreference prefUserNumber;
    private ListPreference prefUserClass;
    private MultiSelectListPreference prefUserGroups;
    private Preference prefNotificationLN;
    private Preference prefNotificationReplacements;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = new Settings(this);
        savedTimetables = new SavedTimetables(this);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        Preference prefVersion = findPreference("version");
        String version = AppVersion.getVersionName(this);
        prefVersion.setSummary(version);

        prefUserNumber = (EditTextPreference) findPreference(getString(R.string.user_number_key));
        prefUserClass = (ListPreference) findPreference(getString(R.string.user_class_key));
        prefUserGroups = (MultiSelectListPreference) findPreference(getString(R.string.user_groups_key));
        prefNotificationLN = findPreference("notification_ln");
        prefNotificationReplacements = findPreference("notification_replacements");

        prefNotificationLN.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingsActivity.this, SettingsNotificationLNActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            }
        });

        prefNotificationReplacements.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(SettingsActivity.this,
                        SettingsNotificationReplacementsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                return true;
            }
        });

        //user number
        prefUserNumber.setSummary(prefUserNumber.getText());
        prefUserNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newString = (String) newValue;
                if (!newString.isEmpty()) {
                    prefUserNumber.setSummary(newString);
                    return true;
                } else {
                    return false;
                }
            }
        });

        //user class
        Collection<String> availableTimetables = savedTimetables.getAvailableTimetables();
        CharSequence[] charSequenceItems = availableTimetables.toArray(new CharSequence[availableTimetables.size()]);
        prefUserClass.setEntries(charSequenceItems);
        prefUserClass.setEntryValues(charSequenceItems);
        prefUserClass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefUserClass.setValue((String) newValue);
                updateUserGroupsEntries();
                return false;
            }
        });

        //user groups
        updateUserGroupsEntries();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateNotificationLNSummary();
        updateNotificationReplacementsSummary();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!settings.getSettingsChanged()) {
            settings.edit()
                    .setSettingsChanged(true)
                    .apply();
        }
    }

    private void updateUserGroupsEntries() {
        Timetable timetable = savedTimetables.load(prefUserClass.getValue());

        if (timetable == null) {
            prefUserGroups.setEntries(new CharSequence[0]);
            prefUserGroups.setEntryValues(new CharSequence[0]);
        } else {
            Collection<String> groups = timetable.getAllGroups();
            CharSequence[] values = groups.toArray(new CharSequence[groups.size()]);
            prefUserGroups.setEntries(values);
            prefUserGroups.setEntryValues(values);
        }
    }

    private void updateNotificationReplacementsSummary() {
        boolean notify = settings.getReplacementsNotify();
        boolean sound = settings.getReplacementsNotifySound();
        boolean vibration = settings.getReplacementsVibration();

        String summary = buildSummary(notify, sound, vibration);
        prefNotificationReplacements.setSummary(summary);
    }

    private void updateNotificationLNSummary() {
        boolean notify = settings.getLNNotify();
        boolean sound = settings.getLNNotifySound();
        boolean vibration = settings.getLNNotifyVibration();

        String summary = buildSummary(notify, sound, vibration);
        prefNotificationLN.setSummary(summary);
    }

    private String buildSummary(boolean notify, boolean sound, boolean vibration) {
        String summary;
        if (notify) {
            if (sound && vibration) {
                summary = getString(R.string.notify_sound_vibration);
            } else if (sound) {
                summary = getString(R.string.notify_sound);
            } else if (vibration) {
                summary = getString(R.string.notify_vibration);
            } else {
                summary = getString(R.string.notify);
            }
        } else {
            summary = getString(R.string.do_not_notify);
        }

        return summary;
    }
}
