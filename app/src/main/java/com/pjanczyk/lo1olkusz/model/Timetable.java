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

import java.util.Set;
import java.util.TreeSet;

/**
 * Timetable of a specific class
 */
public class Timetable implements Parcelable, Emptyable {

    @SerializedName("class")
    private String className;
    @SerializedName("value")
    private TimetableDay[] days;

    public Timetable() { }

    public boolean isEmpty() {
        return days == null || days.length == 0;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public TimetableDay getDay(int day) {
        return day < days.length ? days[day] : null;
    }

    /**
     * @return all existing groups in this timetable
     */
    public Set<String> getAllGroups() {
        Set<String> groups = new TreeSet<>();

        for (TimetableDay d : days) {
            for (TimetableSubject[] h : d.getSubjects().values()) {
                for (TimetableSubject s : h) {
                    String group = s.getGroup();
                    if (group != null) {
                        groups.add(group);
                    }
                }
            }
        }

        return groups;
    }


    //parcelable part

    public Timetable(Parcel in) {
        className = in.readString();
        days = in.createTypedArray(TimetableDay.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(className);
        dest.writeParcelableArray(days, 0);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Timetable createFromParcel(Parcel source) {
            return new Timetable(source);
        }

        @Override
        public Timetable[] newArray(int size) {
            return new Timetable[size];
        }
    };
}

