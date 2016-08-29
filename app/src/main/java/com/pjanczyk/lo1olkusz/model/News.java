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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class News {
    public final int timestamp;
    @Nullable public final Bells bells;
    @NonNull public final List<Timetable> timetables;
    @NonNull public final List<LuckyNumber> luckyNumbers;
    @NonNull public final List<Replacements> replacements;

    public News(int timestamp,
                @Nullable Bells bells,
                @Nullable List<Timetable> timetables,
                @Nullable List<LuckyNumber> luckyNumbers,
                @Nullable List<Replacements> replacements) {

        this.timestamp = timestamp;

        this.bells = bells;

        if (timetables == null) {
            this.timetables = Collections.emptyList();
        } else {
            this.timetables = Collections.unmodifiableList(new ArrayList<>(timetables));
        }

        if (luckyNumbers == null) {
            this.luckyNumbers = Collections.emptyList();
        } else {
            this.luckyNumbers = Collections.unmodifiableList(new ArrayList<>(luckyNumbers));
        }

        if (replacements == null) {
            this.replacements = Collections.emptyList();
        } else {
            this.replacements = Collections.unmodifiableList(new ArrayList<>(replacements));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        if (timestamp != news.timestamp) return false;
        if (bells != null ? !bells.equals(news.bells) : news.bells != null) return false;
        if (!timetables.equals(news.timetables)) return false;
        if (!luckyNumbers.equals(news.luckyNumbers)) return false;
        if (!replacements.equals(news.replacements)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = bells != null ? bells.hashCode() : 0;
        result = 31 * result + timetables.hashCode();
        result = 31 * result + luckyNumbers.hashCode();
        result = 31 * result + replacements.hashCode();
        result = 31 * result + timestamp;
        return result;
    }
}
