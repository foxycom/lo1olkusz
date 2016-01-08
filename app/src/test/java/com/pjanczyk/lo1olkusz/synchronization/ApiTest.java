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

package com.pjanczyk.lo1olkusz.synchronization;


import com.google.gson.JsonSyntaxException;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.model.TimetableSubject;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ApiTest {

    @Test
    public void testBuildUrl() {
        String expected = "http://lo1olkusz-pjanczyk.rhcloud.com/api/news/2222?aid=AID&v=1111";
        String actual = Api.buildUrl("AID", 1111, 2222);
        assertEquals(expected, actual);
    }

    @Test(expected = JsonSyntaxException.class)
    public void testIncorrectJson() {
        String json = "fg49adsf";
        Api.decodeJson(json);
    }

    @Test
    public void testEmptyJson() {
        String json = "{}";
        News news = Api.decodeJson(json);
        assertNotNull(news.luckyNumbers);
        assertNotNull(news.replacements);
        assertNotNull(news.timetables);
        assertNull(news.version);
        assertEquals(news.timestamp, 0);
        assertNull(news.bells);
    }

    @Test
    public void testDecodeJson() {
        String json = "{  \n" +
                "   \"timestamp\":1452114794,\n" +
                "   \"replacements\":[  \n" +
                "      {  \n" +
                "         \"class\":\"1a\",\n" +
                "         \"date\":\"2016-01-04\",\n" +
                "         \"value\":{  \n" +
                "            \"1\":\"ddd\"\n" +
                "         },\n" +
                "         \"lastModified\":1451857744\n" +
                "      }\n" +
                "   ],\n" +
                "   \"luckyNumbers\":[  \n" +
                "      {  \n" +
                "         \"date\":\"2016-01-07\",\n" +
                "         \"value\":22,\n" +
                "         \"lastModified\":1452034208\n" +
                "      }\n" +
                "   ],\n" +
                "   \"timetables\":[  \n" +
                "      {  \n" +
                "         \"class\":\"1a\",\n" +
                "         \"lastModified\":1451250937,\n" +
                "         \"value\":[  \n" +
                "            {  \n" +
                "               \"1\":[  \n" +
                "                  {  \n" +
                "                     \"name\":\"Niemiecki\",\n" +
                "                     \"group\":\"N6\",\n" +
                "                     \"classroom\":\"44\"\n" +
                "                  }\n" +
                "               ]\n" +
                "            }\n" +
                "         ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"bells\":{  \n" +
                "      \"lastModified\":1450821445,\n" +
                "      \"value\":[  \n" +
                "         [  \n" +
                "            \"8:10\",\n" +
                "            \"8:55\"\n" +
                "         ]\n" +
                "      ]\n" +
                "   },\n" +
                "   \"version\":10\n" +
                "}";

        News news = Api.decodeJson(json);

        assertEquals(news.timestamp, 1452114794);

        assertEquals(news.version.intValue(), 10);

        assertEquals(news.luckyNumbers.size(), 1);
        LuckyNumber ln = news.luckyNumbers.get(0);
        assertEquals(ln.getDate(), new LocalDate("2016-01-07"));
        assertEquals(ln.getValue(), 22);

        assertEquals(news.replacements.size(), 1);
        Replacements replacements = news.replacements.get(0);
        assertEquals(replacements.getDate(), new LocalDate("2016-01-04"));
        assertEquals(replacements.getClassName(), "1a");
        assertEquals(replacements.atHour(1), "ddd");

        Bells bells = news.bells;
        assertEquals(bells.getHourBegin(1), new LocalTime("8:10"));
        assertEquals(bells.getHourEnd(1), new LocalTime("8:55"));

        assertEquals(news.timetables.size(), 1);
        Timetable timetable = news.timetables.get(0);
        assertEquals(timetable.getClassName(), "1a");
        TimetableDay day = timetable.getDay(0);
        assertEquals(day.getSubjects().size(), 1);
        TimetableSubject[] subjects = day.getSubjects().get(1);
        assertEquals(subjects.length, 1);
        assertEquals(subjects[0].getName(), "Niemiecki");
        assertEquals(subjects[0].getClassroom(), "44");
        assertEquals(subjects[0].getGroup(), "N6");
    }
}
