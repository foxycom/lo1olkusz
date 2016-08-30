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

import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.model.TimetableSubject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.pjanczyk.testutils.CollectionsUtils.emptyList;
import static com.pjanczyk.testutils.CollectionsUtils.entry;
import static com.pjanczyk.testutils.CollectionsUtils.list;
import static com.pjanczyk.testutils.CollectionsUtils.map;
import static org.junit.Assert.assertEquals;

public class NewsSerializerTest {

    static final String JSON = "{  " +
            "   \"timestamp\":123," +
            "   \"replacements\":[  " +
            "      {  " +
            "         \"class\":\"CLASS\"," +
            "         \"date\":\"2016-01-04\"," +
            "         \"value\":{  " +
            "            \"2\":\"REPLACEMENT\"" +
            "         }," +
            "         \"lastModified\":1451857744" +
            "      }" +
            "   ]," +
            "   \"luckyNumbers\":[  " +
            "      {  " +
            "         \"date\":\"2016-01-07\"," +
            "         \"value\":22," +
            "         \"lastModified\":1452034208" +
            "      }" +
            "   ]," +
            "   \"timetables\":[  " +
            "      {  " +
            "         \"class\":\"CLASS\"," +
            "         \"lastModified\":1451250937," +
            "         \"value\":[  " +
            "            {  " +
            "               \"1\":[  " +
            "                  {  " +
            "                     \"name\":\"SUBJECT\"," +
            "                     \"group\":\"GROUP\"," +
            "                     \"classroom\":\"CLASSROOM\"" +
            "                  }" +
            "               ]" +
            "            }" +
            "         ]" +
            "      }" +
            "   ]," +
            "   \"bells\":{  " +
            "      \"lastModified\":1450821445," +
            "      \"value\":[  " +
            "         [  " +
            "            \"8:00\"," +
            "            \"9:00\"" +
            "         ]" +
            "      ]" +
            "   }," +
            "   \"version\":66" +
            "}";

    static final String JSON_EMPTY = "{\"timestamp\":123}";

    NewsSerializer serializer;

    @Before
    public void setUp() {
        serializer = new NewsSerializer();
    }

    @Test
    public void testSerialize() throws Exception {

        int timestamp = 123;

        Bells bells = new Bells(map(
                entry(1, new Bells.Hour(new LocalTime("8:00"), new LocalTime("9:00")))
        ));

        List<Timetable> timetables = list(
                new Timetable("CLASS", list(
                        new TimetableDay(map(
                                entry(1, list(new TimetableSubject("SUBJECT", "CLASSROOM", "GROUP")))
                        ))
                ))
        );

        List<LuckyNumber> luckyNumbers = list(
                new LuckyNumber(new LocalDate("2016-01-07"), 22)
        );

        List<Replacements> replacements = list(
                new Replacements("CLASS", new LocalDate("2016-01-04"), map(
                        entry(2, "REPLACEMENT")
                ))
        );

        News expected = new News(
                timestamp,
                bells,
                timetables,
                luckyNumbers,
                replacements
        );


        News actual = serializer.deserialize(JSON);

        assertEquals(expected, actual);
    }

    @Test
    public void testSerializeEmpty() throws Exception {
        News expected = new News(
                123,
                null,
                emptyList(Timetable.class),
                emptyList(LuckyNumber.class),
                emptyList(Replacements.class)
        );

        News actual = serializer.deserialize(JSON_EMPTY);

        assertEquals(expected, actual);
    }
}