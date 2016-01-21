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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class TimetableDay implements Parcelable {
    private final TreeMap<Integer, TimetableSubject[]> subjects;

    public TimetableDay(@NonNull Map<Integer, TimetableSubject[]> subjects) {
        this.subjects = new TreeMap<>(subjects);
    }

    public Map<Integer, TimetableSubject[]> getSubjects() {
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

    @Nullable
    public TimetableSubject[] atHour(int hour) {
        return subjects.get(hour);
    }

    //parcelable part

    public TimetableDay(Parcel in) {
        subjects = new TreeMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            int key = in.readInt();
            TimetableSubject[] value = in.createTypedArray(TimetableSubject.CREATOR);
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
        for (Map.Entry<Integer, TimetableSubject[]> entry : subjects.entrySet()) {
            dest.writeInt(entry.getKey());
            dest.writeParcelableArray(entry.getValue(), 0);
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


    //json serialization

    public static class Deserializer implements JsonDeserializer<TimetableDay> {
        @Override
        public TimetableDay deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {
            Type type = new TypeToken<TreeMap<Integer, TimetableSubject[]>>(){}.getType();
            TreeMap<Integer, TimetableSubject[]> subjects = context.deserialize(json, type);
            return new TimetableDay(subjects);
        }
    }

    public static class Serializer implements JsonSerializer<TimetableDay> {
        @Override
        public JsonElement serialize(TimetableDay src, Type typeOfSrc, JsonSerializationContext context) {
            Type type = new TypeToken<TreeMap<Integer, TimetableSubject>>(){}.getType();
            return context.serialize(src.subjects, type);
        }
    }
}
