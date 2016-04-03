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

package com.pjanczyk.testutils;

import android.os.Parcel;
import android.os.Parcelable;

import org.junit.Assert;

public class ParcelableUtils {

    public static void testParcelable(Parcelable object) {
        Parcel parcel = Parcel.obtain();
        parcel.writeParcelable(object, 0);
        parcel.setDataPosition(0);
        Parcelable copy = parcel.readParcelable(object.getClass().getClassLoader());
        parcel.recycle();

        Assert.assertEquals(object, copy);
    }
}
