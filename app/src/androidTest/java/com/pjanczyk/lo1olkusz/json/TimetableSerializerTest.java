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

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.model.TimetableSubject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.pjanczyk.testutils.CollectionsUtils.entry;
import static com.pjanczyk.testutils.CollectionsUtils.list;
import static com.pjanczyk.testutils.CollectionsUtils.map;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class TimetableSerializerTest {

    static final String JSON_1 =
            "{" +
                    "\"class\":\"CLASS\"," +
                    "\"value\":[{" +
                    "\"2\":[{" +
                    "\"name\":\"SUBJECT\"," +
                    "\"classroom\":\"CLASSROOM\"," +
                    "\"group\":\"GROUP\"" +
                    "}]" +
                    "}]" +
                    "}";
    static final Timetable OBJECT_1 = new Timetable(
            "CLASS",
            list(
                    new TimetableDay(map(
                            entry(2, list(new TimetableSubject("SUBJECT", "CLASSROOM", "GROUP")))
                    ))
            )
    );

    static final String JSON_2 = "{\"date\":\"2016-01-02\",\"class\":\"CLASS\",\"value\":null}";
    static final Timetable OBJECT_2 = new Timetable("CLASS", null);

    static final String JSON_INVALID = "{}";

    TimetableSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new TimetableSerializer();
    }

    @Test
    public void testDeserialize() throws Exception {
        assertEquals(OBJECT_1, serializer.deserialize(JSON_1));
        assertEquals(OBJECT_2, serializer.deserialize(JSON_2));
    }

    @Test(expected = JsonParseException.class)
    public void testDeserializeInvalid() throws Exception {
        serializer.deserialize(JSON_INVALID);
    }

    @Test
    public void testSerialize() throws Exception {
        assertEquals(OBJECT_1, serializer.deserialize(serializer.serialize(OBJECT_1)));
        assertEquals(OBJECT_2, serializer.deserialize(serializer.serialize(OBJECT_2)));
    }
}