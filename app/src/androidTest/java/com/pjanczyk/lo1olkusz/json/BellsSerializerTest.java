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

import com.pjanczyk.lo1olkusz.model.Bells;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

import static com.pjanczyk.testutils.CollectionsUtils.*;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class BellsSerializerTest {

    static final String JSON_1 = "{\"value\":[[\"8:00\",\"9:00\"]]}";
    static final Bells OBJECT_1 = new Bells(map(
            entry(1, new Bells.Hour(new LocalTime("8:00"), new LocalTime("9:00")))
    ));

    static final String JSON_2 = "{}";
    static final Bells OBJECT_2 = new Bells(Collections.<Integer, Bells.Hour>emptyMap());

    BellsSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new BellsSerializer();
    }

    @Test
    public void testDeserialize() throws Exception {
        assertEquals(OBJECT_1, serializer.deserialize(JSON_1));
        assertEquals(OBJECT_2, serializer.deserialize(JSON_2));
    }

    @Test
    public void testSerialize() throws Exception {
        assertEquals(OBJECT_1, serializer.deserialize(serializer.serialize(OBJECT_1)));
        assertEquals(OBJECT_2, serializer.deserialize(serializer.serialize(OBJECT_2)));
    }
}