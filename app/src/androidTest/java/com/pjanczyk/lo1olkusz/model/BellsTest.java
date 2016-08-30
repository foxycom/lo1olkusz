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

import com.pjanczyk.testutils.ParcelableUtils;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static com.pjanczyk.testutils.CollectionsUtils.*;
import static org.junit.Assert.*;

public class BellsTest {

    static final LocalTime t1 = new LocalTime("1:00");
    static final LocalTime t2 = new LocalTime("2:00");
    static final LocalTime t3 = new LocalTime("3:00");
    static final LocalTime t4 = new LocalTime("4:00");

    Bells bells;
    Bells empty;

    @Before
    public void setUp() throws Exception {
        bells = new Bells(map(
                entry(1, new Bells.Hour(t1, t2)),
                entry(2, new Bells.Hour(t3, t4))
        ));
        empty = new Bells(Collections.<Integer, Bells.Hour>emptyMap());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, empty.isEmpty());
        assertEquals(false, bells.isEmpty());
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, empty.size());
        assertEquals(2, bells.size());
    }

    @Test
    public void testGet() throws Exception {
        Bells.Hour h1 = bells.get(1);
        Bells.Hour h2 = bells.get(2);
        Bells.Hour h3 = bells.get(3);

        assertEquals(t1, h1.getBegin());
        assertEquals(t2, h1.getEnd());
        assertEquals(t3, h2.getBegin());
        assertEquals(t4, h2.getEnd());
        assertNull(h3.getBegin());
        assertNull(h3.getEnd());
    }

    @Test
    public void testParcelable() throws Exception {
        ParcelableUtils.testParcelable(bells);
        ParcelableUtils.testParcelable(empty);
    }
}