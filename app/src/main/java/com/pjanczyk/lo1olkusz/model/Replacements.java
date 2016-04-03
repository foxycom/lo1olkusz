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

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Replacements of a class on a specific day
 */
public final class Replacements implements Parcelable, Emptyable {

    private final String className;
    private final LocalDate date;
    private final Map<Integer, String> entries;

    public Replacements(@NonNull String className,
                        @NonNull LocalDate date,
                        @Nullable Map<Integer, String> entries) {
        this.className = className;
        this.date = date;
        if (entries == null) {
            this.entries = Collections.emptyMap();
        } else {
            this.entries = new TreeMap<>(entries); // defensive copy
        }
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @NonNull
    public LocalDate getDate() {
        return date;
    }

    @NonNull
    public String getClassName() {
        return className;
    }

    public int size() {
        return entries.size();
    }

    @Nullable
    public String atHour(int hour) {
        return entries.get(hour);
    }

    @NonNull
    public Set<Map.Entry<Integer, String>> entrySet() {
        return Collections.unmodifiableSet(entries.entrySet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Replacements that = (Replacements) o;

        return className.equals(that.className)
                && date.equals(that.date)
                && entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + entries.hashCode();
        return result;
    }

    //parcelable part

    public Replacements(Parcel in) {
        className = in.readString();
        date = new LocalDate(in.readInt(), in.readInt(), in.readInt());

        int size = in.readInt();
        entries = new TreeMap<>();
        for (int i = 0; i < size; i++) {
            entries.put(in.readInt(), in.readString());
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

        dest.writeInt(entries.size());
        for (Map.Entry<Integer, String> entry : entries.entrySet()) {
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
