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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Bells implements Parcelable, Emptyable {

    private static final Hour UNDEFINED_HOUR = new Hour(null, null);

    private final Hour[] hours;

    public Bells(@NonNull Collection<Hour> hours) {
        this.hours = hours.toArray(new Hour[hours.size()]); // defensive copy
    }

    public boolean isEmpty() {
        return hours.length == 0;
    }

    public int size() {
        return hours.length;
    }

    @NonNull
    public Hour get(int hour) {
        int idx = hour - 1;
        if (idx >= 0 && idx < hours.length) {
            return hours[idx];
        }
        else {
            return UNDEFINED_HOUR;
        }
    }

    @Nullable
    @Deprecated
    public LocalTime getHourBegin(int hour) {
        return get(hour).begin;
    }

    @Nullable
    @Deprecated
    public LocalTime getHourEnd(int hour) {
        return get(hour).end;
    }

    public static class Hour {
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
    }

    //parcelable part

    public Bells(Parcel in) {
        hours = new Hour[in.readInt()];
        for (int i = 0; i < hours.length; i++) {
            int begin = in.readInt();
            int end = in.readInt();
            hours[i] = new Hour(
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
        dest.writeInt(hours.length);
        for (Hour hour : hours) {
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

    // Json serializer & deserializer

    public static class Deserializer implements JsonDeserializer<Bells> {
        @Override
        public Bells deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
            JsonElement value = json.getAsJsonObject().get("value");
            if (value.isJsonNull()) {
                return null;
            }
            else {
                JsonArray array = value.getAsJsonArray();
                List<Hour> hours = new ArrayList<>(array.size());

                for (JsonElement element : array) {
                    JsonArray subArray = element.getAsJsonArray();

                    LocalTime begin = context.deserialize(subArray.get(0), LocalTime.class);
                    LocalTime end = context.deserialize(subArray.get(1), LocalTime.class);
                    hours.add(new Hour(begin, end));
                }

                return new Bells(hours);
            }
        }
    }

    public static class Serializer implements JsonSerializer<Bells> {
        @Override
        public JsonElement serialize(Bells src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray result = new JsonArray();

            for (Hour entry : src.hours) {
                JsonArray subArray = new JsonArray();
                subArray.add(context.serialize(entry.begin));
                subArray.add(context.serialize(entry.end));
            }

            return result;
        }
    }
}
