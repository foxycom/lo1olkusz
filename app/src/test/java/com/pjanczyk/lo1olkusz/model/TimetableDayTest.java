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

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TimetableDayTest {

    TimetableDay emptyDay;
    TimetableDay day;
    Map<Integer, TimetableSubject[]> subjects;
    TimetableSubject[] hour1;
    TimetableSubject[] hour2;

    @Before
    public void setUp() throws Exception {
        emptyDay = new TimetableDay(Collections.<Integer, TimetableSubject[]>emptyMap());

        hour1 = new TimetableSubject[0];
        hour2 = new TimetableSubject[0];
        subjects = new HashMap<>();
        subjects.put(3, hour1);
        subjects.put(7, hour2);
        day = new TimetableDay(subjects);
    }

    @Test
    public void testGetSubjects() throws Exception {
        assertEquals(subjects, day.getSubjects());
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, emptyDay.isEmpty());
        assertEquals(false, day.isEmpty());
    }

    @Test
    public void testFirstHour() throws Exception {
        assertEquals(3, day.firstHour());
    }

    @Test
    public void testLastHour() throws Exception {
        assertEquals(7, day.lastHour());
    }

    @Test
    public void testAtHour() throws Exception {
        assertArrayEquals(hour1, day.atHour(3));
        assertArrayEquals(hour2, day.atHour(7));
        assertArrayEquals(null, day.atHour(-1));
    }
}