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

import com.pjanczyk.lo1olkusz.model.Bells;

import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BellsSerializer extends Serializer<Bells> {

    @Override
    public Bells read(JsonReader reader) throws IOException, JsonParseException {
        Map<Integer, Bells.Hour> bells = new HashMap<>();

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String prop = reader.nextName();
                if (prop.equals("value") && reader.peek() != JsonToken.NULL) {
                    reader.beginArray();
                    for (int id = 1; reader.hasNext(); id++) {
                        reader.beginArray();
                        LocalTime begin = LocalTimeSerializer.INSTANCE.read(reader);
                        LocalTime end = LocalTimeSerializer.INSTANCE.read(reader);
                        reader.endArray();
                        bells.put(id, new Bells.Hour(begin, end));
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

        return new Bells(bells);
    }

    @Override
    public void write(JsonWriter writer, Bells object) throws IOException {
        writer.beginObject();

        writer.name("value");
        writer.beginArray();
        for (int i = 1; i <= object.size(); i++) {
            Bells.Hour hour = object.get(i);
            LocalTime begin = hour.getBegin();
            LocalTime end = hour.getEnd();

            writer.beginArray();
            if (begin == null) {
                writer.nullValue();
            } else {
                LocalTimeSerializer.INSTANCE.write(writer, begin);
            }
            if (end == null) {
                writer.nullValue();
            } else {
                LocalTimeSerializer.INSTANCE.write(writer, end);
            }
            writer.endArray();

        }
        writer.endArray();

        writer.endObject();
    }
}
