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

import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;

import java.util.Map;
import java.util.TreeMap;

/**
 * Replacements of a class on a specific day
 */
public class Replacements implements Parcelable, Emptyable {

    @SerializedName("class")
    private String className;
    private LocalDate date;
    private Map<Integer, String> value;

    public Replacements() { }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int size() {
        return value.size();
    }

    public String atHour(int hour) {
        return value.get(hour);
    }

    public void setValue(Map<Integer, String> value) {
        this.value = value;
    }

    //parcelable part

    public Replacements(Parcel in) {
        className = in.readString();
        date = new LocalDate(in.readInt(), in.readInt(), in.readInt());

        int size = in.readInt();
        value = new TreeMap<>();
        for (int i = 0; i < size; i++) {
            value.put(in.readInt(), in.readString());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(className);

        dest.writeInt(date.getYear());
        dest.writeInt(date.getMonthOfYear());
        dest.writeInt(date.getDayOfMonth());

        dest.writeInt(value.size());
        for (Map.Entry<Integer, String> entry : value.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Replacements createFromParcel(Parcel source) {
            return new Replacements(source);
        }

        @Override
        public Replacements[] newArray(int size) {
            return new Replacements[size];
        }
    };
}
