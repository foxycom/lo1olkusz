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

package com.pjanczyk.lo1olkusz.utils.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;

import com.pjanczyk.lo1olkusz.R;

import java.util.Set;

/**
 * Custom multi select list preference.
 * If {@attr R.styleable.Preference_autoSummary} is set true,
 * then shows the current entry in a summary.
 */
public class MultiSelectListPreference extends android.preference.MultiSelectListPreference {
    private final boolean mAutoSummary;

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListPreference, android.R.attr.dialogPreferenceStyle, 0);
        mAutoSummary = a.getBoolean(R.styleable.ListPreference_autoSummary, false);
        a.recycle();
    }

    public MultiSelectListPreference(Context context) {
        this(context, null);
    }

    /**
     * Sets the value of the key. This should contain entries in
     * {@link #getEntryValues()}.
     *
     * @param values The values to set for the key.
     */
    public void setValues(Set<String> values) {
        super.setValues(values);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            notifyChanged();
        }
    }

    @Override
    public CharSequence getSummary() {
        final Set<String> values = getValues();
        final CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();

        if (mAutoSummary && !values.isEmpty()) {

            StringBuilder builder = new StringBuilder();
            boolean firstTime = true;
            for (int i = 0; i < entryValues.length; i++) {
                if (values.contains(entryValues[i].toString())) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(entries[i].toString());
                }
            }
            return builder.toString();

        } else {
            return super.getSummary();
        }
    }



}
