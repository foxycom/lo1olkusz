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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.view.MenuItem;

import com.pjanczyk.lo1olkusz.BuildConfig;
import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.utils.Settings;
import com.pjanczyk.lo1olkusz.synchronization.SyncTimingHelper;
import com.pjanczyk.lo1olkusz.utils.Notifications;

import org.joda.time.LocalDate;

import com.pjanczyk.lo1olkusz.utils.settings.AppCompatPreferenceActivity;

import java.util.Map;
import java.util.TreeMap;

public class SettingsNotificationReplacementsActivity extends AppCompatPreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.settings_notification_replacements);

        CheckBoxPreference prefNotify = (CheckBoxPreference) findPreference(getString(R.string.replacements_notify_key));
        prefNotify.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ((CheckBoxPreference) preference).setChecked(((boolean) newValue));
                SyncTimingHelper.handleAction(getApplicationContext(),
                        SyncTimingHelper.SETTINGS_CHANGED);
                return false;
            }
        });

        Preference prefTest = findPreference("test");
        if (BuildConfig.DEBUG) {
            prefTest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    testNotificationRepls();
                    return true;
                }
            });
        }
        else {
            getPreferenceScreen().removePreference(prefTest);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0); //disable closing transition
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void testNotificationRepls() {
        Settings settings = new Settings(this);

        LocalDate date = LocalDate.now();
        String className = settings.getUserClass();
        Map<Integer, String> value = new TreeMap<>();
        value.put(1, "test");

        Replacements repls = new Replacements(className, date, value);

        Notifications.handleUserReplacements(this, repls);
    }
}
