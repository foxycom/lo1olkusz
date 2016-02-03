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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class HourData {

    private int hourNo;
    private Bells.Hour bells;
    private Collection<TimetableSubject> primarySubjects;
    private Collection<TimetableSubject> secondarySubjects;
    private String replacement;

    public int getHourNo() {
        return hourNo;
    }

    @Nullable
    public LocalTime getBeginTime() {
        return bells.getBegin();
    }

    @Nullable
    public LocalTime getEndTime() {
        return bells.getEnd();
    }

    @NonNull
    public Collection<TimetableSubject> getPrimarySubjects() {
        return Collections.unmodifiableCollection(primarySubjects);
    }

    @NonNull
    public Collection<TimetableSubject> getSecondarySubjects() {
        return Collections.unmodifiableCollection(secondarySubjects);
    }

    @Nullable
    public String getReplacement() {
        return replacement;
    }


    public static class Factory {

        private TimetableDay timetable;
        private Bells bells;
        private Set<String> groups;
        private Replacements replacements;

        public Factory(@Nullable TimetableDay timetable,
                       @Nullable Bells bells,
                       @Nullable Set<String> groups,
                       @Nullable Replacements replacements) {

            this.timetable = timetable;
            this.bells = bells;
            this.groups = groups;
            this.replacements = replacements;
        }

        @NonNull
        public HourData[] getAll() {
            int size = size();
            HourData[] result = new HourData[size];
            for (int i = 0; i < size; i++) {
                result[i] = get(i);
            }

            return result;
        }

        public int size() {
            if (timetable == null || timetable.isEmpty()) {
                return 0;
            } else {
                int firstHour = timetable.firstHour();
                int lastHour = timetable.lastHour();

                return lastHour - firstHour + 1;
            }
        }

        @NonNull
        public HourData get(int index) {
            int firstHour = timetable.firstHour();
            int hourNo = index + firstHour;

            HourData data = new HourData();
            data.hourNo = hourNo;

            if (timetable != null) {
                TimetableSubject[] subjects = timetable.atHour(hourNo);
                separateSubjects(subjects, data);
            }

            if (bells != null) {
                data.bells = bells.get(hourNo);
            }

            if (replacements != null) {
                data.replacement = replacements.atHour(hourNo);
            }

            return data;
        }

        private void separateSubjects(TimetableSubject[] subjects, HourData data) {
            if (groups == null || groups.size() == 0) {
                data.primarySubjects = Arrays.asList(subjects);
                data.secondarySubjects = Collections.emptyList();
            } else {
                List<TimetableSubject> unknown = new ArrayList<>();
                List<TimetableSubject> match = new ArrayList<>();
                List<TimetableSubject> dontMatch = new ArrayList<>();

                for (TimetableSubject subject : subjects) {
                    if (subject.getGroup() == null) {
                        unknown.add(subject);
                    } else if (groups.contains(subject.getGroup())) {
                        match.add(subject);
                    } else {
                        dontMatch.add(subject);
                    }
                }

                if (match.size() == 0) {
                    data.primarySubjects = unknown;
                    data.secondarySubjects = dontMatch;
                } else {
                    data.primarySubjects = match;
                    data.secondarySubjects = dontMatch;
                    data.secondarySubjects.addAll(unknown);
                }
            }
        }

    }
}
