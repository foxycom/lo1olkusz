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

package com.pjanczyk.lo1olkusz.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.joda.time.LocalDate;

public final class LuckyNumber implements Parcelable, Emptyable {

    private final int value;
    private final LocalDate date;

    public LuckyNumber(@NonNull LocalDate date, int value) {
        this.date = date;
        this.value = value;
    }

    public boolean isEmpty() {
        return value <= 0;
    }

    public int getValue() {
        return value;
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    //parcelable part

    public LuckyNumber(Parcel in) {
        date = new LocalDate(in.readInt(), in.readInt(), in.readInt());
        value = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(date.getYear());
        dest.writeInt(date.getMonthOfYear());
        dest.writeInt(date.getDayOfMonth());

        dest.writeInt(value);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public LuckyNumber createFromParcel(Parcel source) {
            return new LuckyNumber(source);
        }

        @Override
        public LuckyNumber[] newArray(int size) {
            return new LuckyNumber[size];
        }
    };
}
