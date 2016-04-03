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

import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class Serializer<T> {

    public abstract T read(JsonReader reader) throws IOException, JsonParseException;

    public abstract void write(JsonWriter writer, T object) throws IOException;

    @NonNull
    public List<T> readArray(JsonReader reader) throws IOException, JsonParseException {
        List<T> result = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            T item = read(reader);
            result.add(item);
        }
        reader.endArray();

        return result;
    }

    public String serialize(T object) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        try {
            write(jsonWriter, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringWriter.toString();
    }

    public T deserialize(String json) throws JsonParseException {
        JsonReader reader = new JsonReader(new StringReader(json));
        try {
            return read(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
