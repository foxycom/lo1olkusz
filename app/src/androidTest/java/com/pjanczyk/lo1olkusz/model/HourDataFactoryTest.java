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

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.pjanczyk.testutils.CollectionsUtils.*;
import static org.junit.Assert.assertEquals;

public class HourDataFactoryTest {

    static final Bells.Hour HOUR_2_BELLS = new Bells.Hour(new LocalTime(1, 0), new LocalTime(2, 0));
    static final String HOUR_2_REPLACEMENT = "replacement";

    static final String USER_GROUP = "user_group";
    static final String OTHER_GROUP = "other_group";

    static final TimetableSubject SUBJECT_USER_GROUP = new TimetableSubject("S1", null, USER_GROUP);
    static final TimetableSubject SUBJECT_OTHER_GROUP = new TimetableSubject("S2", null, OTHER_GROUP);
    static final TimetableSubject SUBJECT_NO_GROUP = new TimetableSubject("S3", null, null);

    HourData.Factory factory;
    HourData.Factory emptyFactory;

    @Before
    public void setUp() throws Exception {
        //bells
        Bells bells = new Bells(list(null, HOUR_2_BELLS));

        //timetable
        TimetableDay timetableDay = new TimetableDay(map(
                entry(2, list(SUBJECT_USER_GROUP, SUBJECT_OTHER_GROUP, SUBJECT_NO_GROUP)),
                entry(3, list(SUBJECT_OTHER_GROUP, SUBJECT_NO_GROUP))
        ));

        //groups
        Set<String> groups = set(USER_GROUP);

        //replacements
        Replacements replacements = new Replacements(
                "CLASS",
                new LocalDate(2016, 1, 1),
                map(
                        entry(2, HOUR_2_REPLACEMENT)
                ));

        factory = new HourData.Factory(timetableDay, bells, groups, replacements);

        emptyFactory = new HourData.Factory(null, null, null, null);
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, emptyFactory.size());
        assertEquals(2, factory.size());
    }

    @Test
    public void testGet() throws Exception {
        HourData hour2 = factory.get(0); //2nd hour
        assertEquals(2, hour2.getHourNo());
        assertEquals(HOUR_2_BELLS.getBegin(), hour2.getBeginTime());
        assertEquals(HOUR_2_BELLS.getEnd(), hour2.getEndTime());
        assertEquals(HOUR_2_REPLACEMENT, hour2.getReplacement());
        assertEquals(list(SUBJECT_USER_GROUP), hour2.getPrimarySubjects());
        assertEquals(list(SUBJECT_OTHER_GROUP, SUBJECT_NO_GROUP), hour2.getSecondarySubjects());

        HourData hour3 = factory.get(1); //3rd hour
        assertEquals(list(SUBJECT_NO_GROUP), hour3.getPrimarySubjects());
        assertEquals(list(SUBJECT_OTHER_GROUP), hour3.getSecondarySubjects());
    }
}