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

package com.pjanczyk.lo1olkusz.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;

public class TimeFormatter {

    private static final String[] DAYS_OF_WEEK = {
            "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"
    };

    private static final String[] DAYS_OF_WEEK_SHORT = {
            "pon.", "wt.", "śr.", "czw.", "pt.", "sob.", "niedz."
    };

    private static final String[] MONTHS_GENITIVE = {
            "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
            "lipca", "sierpnia", "września", "października", "listopada",
            "grudnia"
    };

    public static String relativeDate(LocalDate date) {
        long milisAgo = DateTime.now().toLocalDate().toDate().getTime() - date.toDate().getTime();
        long daysAgo = milisAgo / (1000 * 60 * 60 * 24);

        if (daysAgo <= -2)
            return "za " + (-daysAgo) + " dni";
        else if (daysAgo == -1)
            return "jutro";
        if (daysAgo == 0)
            return "dzisiaj";
        else if (daysAgo == 1)
            return "wczoraj";
        else //daysAgo >= 2
            return daysAgo + " dni temu";
    }

    public static String timeAgo(Period period) {
        int days = period.toStandardDays().getDays();
        if (days > 0) {
            if (days == 1)
                return "1 dzień temu";
            else
                return days + " dni temu";
        }

        int hours = period.toStandardHours().getHours();
        if (hours > 0) {
            if (hours == 1)
                return "godzinę temu";
            else if (hours % 10 >= 2 && hours % 10 <= 4)
                return hours + " godziny temu";
            else
                return hours + " godzin temu";
        }

        int minutes = period.toStandardMinutes().getMinutes();
        if (minutes > 0) {
            if (minutes == 1)
                return "minutę temu";
            else if (minutes % 10 >= 2 && minutes % 10 <= 4)
                return minutes + " minuty temu";
            else
                return minutes + " minut temu";
        }

        return "mniej niż minutę temu";
    }

    public static String dayOfWeek(LocalDate date) {
        return dayOfWeek(date.getDayOfWeek());
    }

    public static String dayOfWeek(int dayOfWeek) {
        return DAYS_OF_WEEK[dayOfWeek - 1];
    }
	
	public static String dayOfWeekShort(LocalDate date) {
		return dayOfWeekShort(date.getDayOfWeek());
	}
	
	public static String dayOfWeekShort(int dayOfWeek) {
		return DAYS_OF_WEEK_SHORT[dayOfWeek - 1];
	}

    /**
     * Returns date in format "d MMMM", e.g. "1 września"
     */
    public static String dayAndMonth(LocalDate date) {
        return date.getDayOfMonth() + " " + MONTHS_GENITIVE[date.getMonthOfYear() - 1];
    }

    public static String dayOfWeekDayAndMonth(LocalDate date) {
        return dayOfWeek(date) + ", " + dayAndMonth(date);
    }
}
