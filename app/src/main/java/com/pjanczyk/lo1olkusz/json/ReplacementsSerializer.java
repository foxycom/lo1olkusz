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

import com.pjanczyk.lo1olkusz.model.Replacements;

import org.joda.time.LocalDate;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class ReplacementsSerializer extends Serializer<Replacements> {

    public Replacements read(JsonReader reader) throws IOException, JsonParseException {
        String className = null;
        LocalDate date = null;
        Map<Integer, String> value = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String prop = reader.nextName();
                if (prop.equals("class")) {
                    className = reader.nextString();
                } else if (prop.equals("date")) {
                    date = LocalDateSerializer.INSTANCE.read(reader);
                } else if (prop.equals("value") && reader.peek() != JsonToken.NULL) {
                    value = new TreeMap<>();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        int hour = Integer.parseInt(reader.nextName());
                        String text = reader.nextString();
                        value.put(hour, text);
                    }
                    reader.endObject();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IllegalStateException | NumberFormatException e) {
            throw new JsonParseException(e);
        }

        if (className == null) throw new JsonParseException("Unspecified 'class' property");
        if (date == null) throw new JsonParseException("Unspecified 'date' property");

        return new Replacements(className, date, value);
    }

    @Override
    public void write(JsonWriter writer, Replacements object) throws IOException {
        writer.beginObject();

        writer.name("date");
        LocalDateSerializer.INSTANCE.write(writer, object.getDate());

        writer.name("class");
        writer.value(object.getClassName());

        if (!object.isEmpty()) {
            writer.name("value");
            writer.beginObject();
            for (Map.Entry<Integer, String> entry : object.entrySet()) {
                writer.name(entry.getKey().toString());
                writer.value(entry.getValue());
            }
            writer.endObject();
        }

        writer.endObject();
    }

}
