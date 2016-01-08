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

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.view.View;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.storage.SavedTimetables;
import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.utils.settings.PreferenceFragment;

import java.util.Collection;

public class FirstRunSettingsFragment extends PreferenceFragment {

    private SavedTimetables savedTimetables;

    private ListPreference prefUserClass;
    private MultiSelectListPreference prefUserGroups;
    private EditTextPreference prefUserNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedTimetables = new SavedTimetables(getContext());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addPreferencesFromResource(R.xml.first_run_settings);

        prefUserClass = (ListPreference) findPreference(getString(R.string.user_class_key));
        prefUserGroups = (MultiSelectListPreference) findPreference(getString(R.string.user_groups_key));
        prefUserNumber = (EditTextPreference) findPreference(getString(R.string.user_number_key));

        //user class
        prefUserClass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefUserClass.setValue((String) newValue); //update the value
                updateGroupsEntries();
                checkSettingsSet();
                return false; //don't update value again
            }
        });

        //user number
        prefUserNumber.setSummary(prefUserNumber.getText());
        prefUserNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefUserNumber.setText((String) newValue); //update the value
                updateUserNumberSummary();
                checkSettingsSet();
                return false; //don't update value again
            }
        });

        updateClassEntries();
        updateGroupsEntries();
        updateUserNumberSummary();
        checkSettingsSet();
    }

    private void checkSettingsSet() {
        boolean set = prefUserClass.getValue() != null
                && prefUserNumber.getText() != null
                && !prefUserNumber.getText().isEmpty();

        FirstRunUserDetailsFragment parent = (FirstRunUserDetailsFragment) getParentFragment();
        parent.setSettingsSet(set);
    }

    private void updateClassEntries() {
        Collection<String> availableTimetables = savedTimetables.getAvailableTimetables();
        CharSequence[] charSequenceItems = availableTimetables.toArray(new CharSequence[availableTimetables.size()]);
        prefUserClass.setEntries(charSequenceItems);
        prefUserClass.setEntryValues(charSequenceItems);
    }

    private void updateGroupsEntries() {
        Timetable timetable = savedTimetables.load(prefUserClass.getValue());

        if (timetable == null) {
            prefUserGroups.setEnabled(false);
            prefUserGroups.setEntries(new CharSequence[0]);
            prefUserGroups.setEntryValues(new CharSequence[0]);
        }
        else {
            prefUserGroups.setEnabled(true);
            Collection<String> groups = timetable.getAllGroups();
            CharSequence[] values = groups.toArray(new CharSequence[groups.size()]);
            prefUserGroups.setEntries(values);
            prefUserGroups.setEntryValues(values);
        }
    }

    private void updateUserNumberSummary() {
        if (prefUserNumber.getText() == null || prefUserNumber.getText().isEmpty()) {
            prefUserNumber.setSummary(R.string.choose_number);
        }
        else {
            prefUserNumber.setSummary(prefUserNumber.getText());
        }
    }
}
