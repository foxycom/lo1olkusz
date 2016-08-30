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

import org.joda.time.LocalTime;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class Bells implements Parcelable, Emptyable {

    private final Map<Integer, Hour> hours;

    public Bells(@NonNull Map<Integer, Hour> hours) {
        this.hours = new TreeMap<>(hours);
    }

    public boolean isEmpty() {
        return hours.isEmpty();
    }

    public int size() {
        return hours.size();
    }

    @NonNull
    public Hour get(int hour) {
        Hour temp = hours.get(hour);
        if (temp != null) {
            return temp;
        } else {
            return Hour.EMPTY;
        }
    }

    @NonNull
    public Set<Map.Entry<Integer, Hour>> getEntries() {
        return Collections.unmodifiableSet(hours.entrySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bells bells = (Bells) o;

        return hours.equals(bells.hours);

    }

    @Override
    public int hashCode() {
        return hours.hashCode();
    }

    public final static class Hour {
        public static final Hour EMPTY = new Hour(null, null);

        private final LocalTime begin;
        private final LocalTime end;

        public Hour(@Nullable LocalTime begin, @Nullable LocalTime end) {
            this.begin = begin;
            this.end = end;
        }

        @Nullable
        public LocalTime getBegin() {
            return begin;
        }

        @Nullable
        public LocalTime getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Hour hour = (Hour) o;

            if (begin != null ? !begin.equals(hour.begin) : hour.begin != null) return false;
            return end != null ? end.equals(hour.end) : hour.end == null;

        }

        @Override
        public int hashCode() {
            int result = begin != null ? begin.hashCode() : 0;
            result = 31 * result + (end != null ? end.hashCode() : 0);
            return result;
        }
    }

    //parcelable part

    public Bells(Parcel in) {
        hours = new TreeMap<>();
        int count = in.readInt();

        for (int i = 0; i < count; i++) {
            int id = in.readInt();
            int begin = in.readInt();
            int end = in.readInt();
            Hour hour = new Hour(
                    begin == 0 ? null : LocalTime.fromMillisOfDay(begin),
                    end == 0 ? null : LocalTime.fromMillisOfDay(end));

            hours.put(id, hour);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hours.size());
        for (Map.Entry<Integer, Hour> entry : hours.entrySet()) {
            int id = entry.getKey();
            Hour hour = entry.getValue();

            dest.writeInt(id);
            dest.writeInt(hour.begin == null ? 0 : hour.begin.getMillisOfDay());
            dest.writeInt(hour.end == null ? 0 : hour.end.getMillisOfDay());
        }
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public Bells createFromParcel(Parcel source) {
            return new Bells(source);
        }

        @Override
        public Bells[] newArray(int size) {
            return new Bells[size];
        }
    };
}
