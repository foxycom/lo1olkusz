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

import static com.pjanczyk.testutils.CollectionsUtils.entry;
import static com.pjanczyk.testutils.CollectionsUtils.list;
import static com.pjanczyk.testutils.CollectionsUtils.map;
import static com.pjanczyk.testutils.CollectionsUtils.set;
import static org.junit.Assert.assertEquals;

public class TimetableTest {

    static final TimetableDay DAY_0 = new TimetableDay(map(
            entry(1, list(
                    new TimetableSubject("", null, "G1"),
                    new TimetableSubject("", null, "G1")
            ))
    ));

    static final TimetableDay DAY_1 = new TimetableDay(map(
            entry(1, list(
                    new TimetableSubject("", null, "G2"),
                    new TimetableSubject("", null, "G3")
            )),
            entry(2, list(
                    new TimetableSubject("", null, "G4"),
                    new TimetableSubject("", null, null)
            ))
    ));

    Timetable timetable;
    Timetable empty;

    @Before
    public void setUp() throws Exception {
        empty = new Timetable("CLASSNAME", null);
        timetable = new Timetable("CLASSNAME", list(DAY_0, DAY_1));
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
        assertEquals(DAY_0, timetable.getDay(0));
        assertEquals(DAY_1, timetable.getDay(1));
        assertEquals(null, timetable.getDay(2));
    }

    @Test
    public void testGetAllGroups() throws Exception {
        assertEquals(set(), empty.getAllGroups());
        assertEquals(set("G1", "G2", "G3", "G4"), timetable.getAllGroups());
    }
}