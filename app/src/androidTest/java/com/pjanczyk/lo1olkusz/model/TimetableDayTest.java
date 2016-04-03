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

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.pjanczyk.testutils.CollectionsUtils.entry;
import static com.pjanczyk.testutils.CollectionsUtils.list;
import static com.pjanczyk.testutils.CollectionsUtils.map;
import static org.junit.Assert.*;

public class TimetableDayTest {

    static final TimetableSubject SUBJECT_1 = new TimetableSubject("S1", null, null);
    static final TimetableSubject SUBJECT_2 = new TimetableSubject("S2", null, null);

    static final List<TimetableSubject> HOUR_1 = list(SUBJECT_1);
    static final List<TimetableSubject> HOUR_2 = list(SUBJECT_2);

    static final Map<Integer, List<TimetableSubject>> HOURS = map(
            entry(3, HOUR_1),
            entry(7, HOUR_2)
    );

    TimetableDay emptyDay;
    TimetableDay day;

    @Before
    public void setUp() throws Exception {
        emptyDay = new TimetableDay(Collections.<Integer, List<TimetableSubject>>emptyMap());
        day = new TimetableDay(HOURS);
    }

    @Test
    public void testGetHours() throws Exception {
        assertEquals(HOURS, day.getHours());
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
        assertEquals(HOUR_1, day.atHour(3));
        assertEquals(HOUR_2, day.atHour(7));
        assertEquals(list(), day.atHour(-1));
    }

    @Test
    public void testParcelable() {
        ParcelableUtils.testParcelable(day);
        ParcelableUtils.testParcelable(emptyDay);
    }
}