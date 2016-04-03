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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.pjanczyk.testutils.CollectionsUtils.*;
import static org.junit.Assert.assertEquals;

public class ReplacementsTest {

    static final String CLASS_NAME = "CLASS";
    static final LocalDate DATE = LocalDate.parse("2016-01-01");

    static final Map<Integer, String> ENTRIES = map(
            entry(3, "r3"),
            entry(5, "r5")
    );

    Replacements repls;
    Replacements empty;

    @Before
    public void setUp() throws Exception {
        repls = new Replacements(CLASS_NAME, DATE, ENTRIES);
        empty = new Replacements(CLASS_NAME, DATE, null);
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(false, repls.isEmpty());
        assertEquals(true, empty.isEmpty());
    }

    @Test
    public void testGetDate() throws Exception {
        assertEquals(DATE, repls.getDate());
    }

    @Test
    public void testGetClassName() throws Exception {
        assertEquals(CLASS_NAME, repls.getClassName());
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(2, repls.size());
        assertEquals(0, empty.size());
    }

    @Test
    public void testAtHour() throws Exception {
        assertEquals("r3", repls.atHour(3));
        assertEquals(null, repls.atHour(4));
        assertEquals("r5", repls.atHour(5));
    }

    @Test
    public void testEntrySet() throws Exception {
        assertEquals(ENTRIES.entrySet(), repls.entrySet());
    }

    @Test
    public void testParcelable() {
        ParcelableUtils.testParcelable(repls);
        ParcelableUtils.testParcelable(empty);
    }
}