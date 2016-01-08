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
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;

import java.lang.reflect.Type;

public class Bells implements Parcelable, Emptyable {

    private TimeSpan[] value;

    public Bells() { }

    public boolean isEmpty() {
        return value == null || value.length == 0;
    }

    @Nullable
    public LocalTime getHourBegin(int hour) {
        if (hour - 1 < value.length) {
            return value[hour - 1].begin;
        } else {
            return null;
        }
    }

    @Nullable
    public LocalTime getHourEnd(int hour) {
        if (hour - 1 < value.length) {
            return value[hour - 1].end;
        } else {
            return null;
        }
    }

    //parcelable part

    public Bells(Parcel in) {
        value = new TimeSpan[in.readInt()];
        for (int i = 0; i < value.length; i++) {
            int begin = in.readInt();
            int end = in.readInt();
            value[i] = new TimeSpan(
                    begin == 0 ? null : new LocalTime((long) begin),
                    end == 0 ? null : new LocalTime((long) end));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value.length);
        for (TimeSpan bell : value) {
            dest.writeInt(bell.begin == null ? 0 : bell.begin.getMillisOfDay());
            dest.writeInt(bell.end == null ? 0 : bell.end.getMillisOfDay());
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

    public static class TimeSpan {

        public final LocalTime begin;
        public final LocalTime end;

        public TimeSpan(LocalTime begin, LocalTime end) {
            this.begin = begin;
            this.end = end;
        }

        public static class Deserializer implements JsonDeserializer<TimeSpan> {
            @Override
            public TimeSpan deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {
                JsonArray array = json.getAsJsonArray();
                LocalTime begin = context.deserialize(array.get(0), LocalTime.class);
                LocalTime end = context.deserialize(array.get(1), LocalTime.class);

                return new TimeSpan(begin, end);
            }
        }

        public static class Serializer implements JsonSerializer<TimeSpan> {
            @Override
            public JsonElement serialize(TimeSpan src, Type typeOfSrc, JsonSerializationContext context) {
                JsonArray result = new JsonArray();
                result.add(context.serialize(src.begin));
                result.add(context.serialize(src.end));

                return result;
            }
        }
    }
}
