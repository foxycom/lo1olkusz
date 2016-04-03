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

package com.pjanczyk.lo1olkusz.json;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;

import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.model.TimetableSubject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimetableSerializer extends Serializer<Timetable> {

    @Override
    public Timetable read(JsonReader reader) throws IOException, JsonParseException {
        String className = null;
        List<TimetableDay> days = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String prop = reader.nextName();
                if (prop.equals("class")) {
                    className = reader.nextString();
                } else if (prop.equals("value") && reader.peek() != JsonToken.NULL) {
                    days = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        TimetableDay day = readTimetableDay(reader);
                        days.add(day);
                    }
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IllegalStateException | NumberFormatException e) {
            throw new JsonParseException(e);
        }

        if (className == null) throw new JsonParseException("Unspecified 'class' property");

        return new Timetable(className, days);
    }

    private TimetableSubject readTimetableSubject(JsonReader reader) throws IOException {
        String name = null;
        String classroom = null;
        String group = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String prop = reader.nextName();
            if (prop.equals("name")) {
                name = reader.nextString();
            } else if (prop.equals("classroom") && reader.peek() != JsonToken.NULL) {
                classroom = reader.nextString();
            } else if (prop.equals("group") && reader.peek() != JsonToken.NULL) {
                group = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();

        if (name == null) throw new IOException("Unspecified 'name' property");

        return new TimetableSubject(name, classroom, group);
    }

    private List<TimetableSubject> readTimetableHour(JsonReader reader) throws IOException {
        List<TimetableSubject> subjects = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            TimetableSubject subject = readTimetableSubject(reader);
            subjects.add(subject);
        }
        reader.endArray();

        return subjects;
    }

    private TimetableDay readTimetableDay(JsonReader reader) throws IOException {
        Map<Integer, List<TimetableSubject>> hours = new TreeMap<>();

        reader.beginObject();
        while (reader.hasNext()) {
            int hour = Integer.parseInt(reader.nextName());
            List<TimetableSubject> subjects = readTimetableHour(reader);
            hours.put(hour, subjects);
        }
        reader.endObject();

        return new TimetableDay(hours);
    }

    @Override
    public void write(JsonWriter writer, Timetable object) {
        try {
            writer.beginObject();

            writer.name("class");
            writer.value(object.getClassName());

            writer.name("value");
            writer.beginArray();
            for (TimetableDay day : object.getAllDays()) {
                writeTimetableDay(writer, day);
            }
            writer.endArray();

            writer.endObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeTimetableDay(JsonWriter writer, TimetableDay day) throws IOException {
        writer.beginObject();
        Map<Integer, List<TimetableSubject>> hours = day.getHours();
        for (Map.Entry<Integer, List<TimetableSubject>> hour : hours.entrySet()) {
            writer.name(hour.getKey().toString());
            writer.beginArray();
            for (TimetableSubject subject : hour.getValue()) {
                writeTimetableSubject(writer, subject);
            }
            writer.endArray();
        }
        writer.endObject();
    }

    private void writeTimetableSubject(JsonWriter writer, TimetableSubject subject) throws IOException {
        writer.beginObject();

        writer.name("name");
        writer.value(subject.getName());

        if (subject.getClassroom() != null) {
            writer.name("classroom");
            writer.value(subject.getClassroom());
        }

        if (subject.getGroup() != null) {
            writer.name("group");
            writer.value(subject.getGroup());
        }

        writer.endObject();
    }

}
