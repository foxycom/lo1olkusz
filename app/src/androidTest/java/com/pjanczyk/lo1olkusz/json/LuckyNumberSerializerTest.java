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

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.pjanczyk.lo1olkusz.model.LuckyNumber;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LuckyNumberSerializerTest {

    static final String JSON_1 = "{\"date\":\"2016-01-02\",\"value\":8}";
    static final LuckyNumber OBJECT_1 = new LuckyNumber(new LocalDate("2016-01-02"), 8);

    static final String JSON_2 = "{\"date\":\"2016-01-02\",\"value\":null}";
    static final LuckyNumber OBJECT_2 = new LuckyNumber(new LocalDate("2016-01-02"), -1);

    static final String JSON_INVALID = "{}";

    LuckyNumberSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new LuckyNumberSerializer();
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