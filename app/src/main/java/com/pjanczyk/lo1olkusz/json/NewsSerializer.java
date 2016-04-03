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
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.model.Timetable;

import java.io.IOException;
import java.util.List;

public class NewsSerializer extends Serializer<News> {

    private final Serializer<Bells> bellsSerializer = new BellsSerializer();
    private final Serializer<Timetable> timetableSerializer = new TimetableSerializer();
    private final Serializer<Replacements> replacementsSerializer = new ReplacementsSerializer();
    private final Serializer<LuckyNumber> lnSerializer = new LuckyNumberSerializer();

    @Override
    public News read(JsonReader reader) throws IOException, JsonParseException {
        int timestamp = -1;
        Integer version = null;
        Bells bells = null;
        List<Timetable> timetables = null;
        List<LuckyNumber> luckyNumbers = null;
        List<Replacements> replacements = null;

        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String prop = reader.nextName();

                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull(); //skip nulls
                } else {
                    switch (prop) {
                        case "timestamp":
                            timestamp = reader.nextInt();
                            break;
                        case "version":
                            version = reader.nextInt();
                            break;
                        case "bells":
                            bells = bellsSerializer.read(reader);
                            break;
                        case "timetables":
                            timetables = timetableSerializer.readArray(reader);
                            break;
                        case "luckyNumbers":
                            luckyNumbers = lnSerializer.readArray(reader);
                            break;
                        case "replacements":
                            replacements = replacementsSerializer.readArray(reader);
                            break;
                        default:
                            reader.skipValue();
                            break;
                    }
                }
            }
            reader.endObject();
        } catch (IllegalStateException | NumberFormatException e) {
            throw new JsonParseException(e);
        }

        if (timestamp == -1) throw new JsonParseException("Unspecified 'timestamp' property");

        return new News(
                timestamp,
                version,
                bells,
                timetables,
                luckyNumbers,
                replacements
        );
    }

    @Override
    public void write(JsonWriter writer, News object) {
        throw new UnsupportedOperationException();
    }
}
