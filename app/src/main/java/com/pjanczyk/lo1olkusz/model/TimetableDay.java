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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class TimetableDay implements Parcelable {
    private final TreeMap<Integer, List<TimetableSubject>> subjects;

    public TimetableDay(@Nullable Map<Integer, List<TimetableSubject>> subjects) {
        this.subjects = new TreeMap<>(subjects); // defensive copy
    }

    public Map<Integer, List<TimetableSubject>> getHours() {
        return Collections.unmodifiableMap(subjects);
    }

    public boolean isEmpty() {
        return subjects.isEmpty();
    }

    public int firstHour() {
        return subjects.firstKey();
    }

    public int lastHour() {
        return subjects.lastKey();
    }

    @NonNull
    public List<TimetableSubject> atHour(int hour) {
        List<TimetableSubject> list = subjects.get(hour);
        if (list != null) {
            return Collections.unmodifiableList(list);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimetableDay that = (TimetableDay) o;

        return subjects.equals(that.subjects);
    }

    @Override
    public int hashCode() {
        return subjects.hashCode();
    }

    //parcelable part

    public TimetableDay(Parcel in) {
        subjects = new TreeMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int key = in.readInt();
            List<TimetableSubject> value = in.createTypedArrayList(TimetableSubject.CREATOR);
            subjects.put(key, value);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(subjects.size());
        for (Map.Entry<Integer, List<TimetableSubject>> entry : subjects.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
    }

    public static final Parcelable.Creator<TimetableDay> CREATOR = new Creator<TimetableDay>() {
        @Override
        public TimetableDay createFromParcel(Parcel source) {
            return new TimetableDay(source);
        }

        @Override
        public TimetableDay[] newArray(int size) {
            return new TimetableDay[size];
        }
    };
}
