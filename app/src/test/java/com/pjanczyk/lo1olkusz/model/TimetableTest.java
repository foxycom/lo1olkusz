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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

public class TimetableTest {

    Timetable timetable;
    Timetable empty;
    TimetableDay day0;
    TimetableDay day1;

    @Before
    public void setUp() throws Exception {
        empty = new Timetable("CLASSNAME", new TimetableDay[0]);

        TimetableSubject s1 = new TimetableSubject("", null, "G1");
        TimetableSubject s2 = new TimetableSubject("", null, "G2");
        TimetableSubject s3 = new TimetableSubject("", null, "G3");
        TimetableSubject s4 = new TimetableSubject("", null, "G4");
        TimetableSubject sn = new TimetableSubject("", null, null);

        Map<Integer, TimetableSubject[]> subjects = new TreeMap<>();
        subjects.put(1, new TimetableSubject[]{s1, s1});
        day0 = new TimetableDay(subjects);

        subjects = new TreeMap<>();
        subjects.put(1, new TimetableSubject[]{s2, s3});
        subjects.put(2, new TimetableSubject[]{s4, sn});
        day1 = new TimetableDay(subjects);

        timetable = new Timetable("CLASSNAME", new TimetableDay[]{day0, day1});
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, empty.isEmpty());
        assertEquals(false, timetable.isEmpty());
    }

    @Test
    public void testGetClassName() throws Exception {
        assertEquals("CLASSNAME", timetable.getClassName());
    }

    @Test
    public void testGetDay() throws Exception {
        assertEquals(null, timetable.getDay(-1));
        assertEquals(day0, timetable.getDay(0));
        assertEquals(day1, timetable.getDay(1));
        assertEquals(null, timetable.getDay(2));
    }

    @Test
    public void testGetAllGroups() throws Exception {
        assertEquals(Collections.emptySet(), empty.getAllGroups());

        Set<String> expected = new HashSet<>();
        expected.add("G1");
        expected.add("G2");
        expected.add("G3");
        expected.add("G4");
        assertEquals(expected, timetable.getAllGroups());
    }
}