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
import android.support.annotation.Nullable;

/**
 * Subject at a specific hour.
 */
public final class TimetableSubject implements Parcelable {
    private final String name;
    private final String classroom;
    private final String group;

    public TimetableSubject(@NonNull String name,
                            @Nullable String classroom,
                            @Nullable String group) {
        this.name = name;
        this.classroom = classroom;
        this.group = group;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    public String getClassroom() {
        return classroom;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimetableSubject that = (TimetableSubject) o;

        if (!name.equals(that.name)) return false;
        if (classroom != null ? !classroom.equals(that.classroom) : that.classroom != null)
            return false;
        return !(group != null ? !group.equals(that.group) : that.group != null);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (classroom != null ? classroom.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }

    //parcelable part

    public TimetableSubject(Parcel in) {
        name = in.readString();
        classroom = in.readString();
        group = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(classroom);
        dest.writeString(group);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TimetableSubject> CREATOR = new Parcelable.Creator<TimetableSubject>() {
        public TimetableSubject createFromParcel(Parcel in) {
            return new TimetableSubject(in);
        }

        public TimetableSubject[] newArray(int size) {
            return new TimetableSubject[size];
        }
    };


}
