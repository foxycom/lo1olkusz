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

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.pjanczyk.lo1olkusz.R;

public class DialogLicenses extends DialogPreference {

    public DialogLicenses(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setNegativeButtonText(null);
        this.setPositiveButtonText(null);
        this.setDialogLayoutResource(R.layout.dialog_licences);
    }
}
