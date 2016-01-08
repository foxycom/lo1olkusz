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

/**
 * Custom list preference.
 * If {@attr R.styleable.Preference_autoSummary} is set true,
 * then shows the current entry in a summary.
 */
public class ListPreference extends android.preference.ListPreference {
    private boolean mAutoSummary;

    public ListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ListPreference, android.R.attr.dialogPreferenceStyle, 0);
        mAutoSummary = a.getBoolean(R.styleable.ListPreference_autoSummary, false);
        a.recycle();
    }

    public ListPreference(Context context) {
        this(context, null);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            notifyChanged();
        }
    }

    @Override
    public CharSequence getSummary() {
        if (mAutoSummary && getValue() != null) {
            return getEntry();
        }
        else {
            return super.getSummary();
        }
    }

}
