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

package com.pjanczyk.lo1olkusz.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.HourData;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.model.TimetableSubject;

import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.util.Set;

/**
 * A view of lessons and breaks showed in a vertical list,
 * where the current hour/break is highlighted.
 */
public class HourList extends AbstractTimedList {

    private HourData[] data;
    private boolean today;

    public HourList(Context context) {
        this(context, null);
    }

    public HourList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HourList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final int sizeInDp = 5;
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (sizeInDp * scale + 0.5f);

        setPadding(0, dpAsPixels, 0, dpAsPixels);
    }

    public void setData(boolean today,
                        @Nullable TimetableDay timetable,
                        @Nullable Bells bells,
                        @Nullable Set<String> groups,
                        @Nullable Replacements replacements) {

        this.today = today;

        HourData.Factory factory = new HourData.Factory(timetable, bells, groups, replacements);
        this.data = factory.getAll();

        super.notifyDataSetChanged();
    }

    @Override
    protected View createItemView(int position, ViewGroup parent, boolean active) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewCreator vc;

        if (position % 2 == 0) { //lesson
            vc = new HourViewCreator(data[position / 2]);
        } else { //break
            HourData previous = data[(position - 1) / 2];
            HourData next = data[(position + 1) / 2];
            vc = new BreakViewCreator(previous.getEndTime(), next.getBeginTime());
        }

        return vc.createView(inflater, parent, active);
    }

    @Override
    protected LocalTime getItemBeginTime(int position) {
        if (!today) return null;

        if (position % 2 == 1) { //break
            int previousHour = (position - 1) / 2;
            return data[previousHour].getEndTime();
        } else { //lesson
            int hour = position / 2;
            return data[hour].getBeginTime();
        }
    }

    @Override
    protected LocalTime getItemEndTime(int position) {
        if (!today) return null;

        if (position % 2 == 1) { //break
            int nextHour = (position + 1) / 2;
            return data[nextHour].getBeginTime();
        } else { //lesson
            int hour = position / 2;
            return data[hour].getEndTime();
        }
    }

    @Override
    protected int getItemCount() {
        if (data.length == 0)
            return 0;
        else
            return 2 * data.length - 1;
    }

    private int getColorResource(@ArrayRes int resId, int index) {
        TypedArray array = getResources().obtainTypedArray(resId);
        int color = array.getColor(index, 0);
        array.recycle();

        return color;
    }

    private interface ViewCreator {
        View createView(LayoutInflater inflater,
                        ViewGroup parent,
                        boolean active);

    }

    private class BreakViewCreator implements ViewCreator {

        public final LocalTime beginTime;
        public final LocalTime endTime;

        public BreakViewCreator(LocalTime beginTime, LocalTime endTime) {
            this.beginTime = beginTime;
            this.endTime = endTime;
        }

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent, boolean active) {
            View view = inflater.inflate(R.layout.hour_list_break, parent, false);

            TextView viewMinutes = (TextView) view.findViewById(R.id.minutes_text);
            View line = view.findViewById(R.id.horizontal_line);

            if (beginTime != null && endTime != null) {
                Period period = new Period(beginTime, endTime);
                int minutes = period.getMinutes();

                viewMinutes.setText(String.format("%d", minutes));
            }

            int state = active ? 1 : 0;
            int minutesColor = getColorResource(R.array.hour_list_break_text, state);
            int lineColor = getColorResource(R.array.hour_list_break_line, state);

            viewMinutes.setTextColor(minutesColor);
            line.setBackgroundColor(lineColor);

            return view;
        }
    }

    private class HourViewCreator implements ViewCreator {
        private final HourData data;

        public HourViewCreator(HourData data) {
            this.data = data;
        }

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent, boolean active) {
            boolean primary = !data.getPrimarySubjects().isEmpty();

            View row = inflater.inflate(R.layout.hour_list_hour, parent, false);

            ViewGroup containerCells = (ViewGroup) row.findViewById(R.id.container_cells);
            TextView textHour = (TextView) row.findViewById(R.id.text_hour);
            TextView textBegin = (TextView) row.findViewById(R.id.text_begin);
            TextView textEnd = (TextView) row.findViewById(R.id.text_end);
            TextView textReplacement = (TextView) row.findViewById(R.id.text_replacement);

            //hour
            textHour.setText(String.format("%d.", data.getHourNo()));

            //begin & end time
            LocalTime begin = data.getBeginTime();
            if (begin != null) {
                textBegin.setText(begin.toString("HH:mm"));
            }
            LocalTime end = data.getEndTime();
            if (end != null) {
                textEnd.setText(end.toString("HH:mm"));
            }

            //subjects
            boolean firstCell = true;
            for (TimetableSubject subject : data.getPrimarySubjects()) {
                if (!firstCell) {
                    addSeparator(inflater, containerCells, active);
                }
                addLessonCell(inflater, containerCells, subject, true, active);
                firstCell = false;
            }
            for (TimetableSubject subject : data.getSecondarySubjects()) {
                if (!firstCell) {
                    addSeparator(inflater, containerCells, active);
                }
                addLessonCell(inflater, containerCells, subject, false, active);
                firstCell = false;
            }

            //replacement
            if (data.getReplacement() != null) {
                textReplacement.setVisibility(View.VISIBLE);
                textReplacement.setText(data.getReplacement());
            }

            int state = (active ? 0b10 : 0) + (primary ? 0b01 : 0);
            int hourColor = getColorResource(R.array.hour_list_hour_number, state);
            textHour.setTextColor(hourColor);

            return row;
        }

        private void addSeparator(LayoutInflater inflater,
                                  ViewGroup containerCells,
                                  boolean active) {

            View separator = inflater.inflate(R.layout.hour_list_lesson_separator,
                    containerCells, false);

            int state = active ? 1 : 0;
            int color = getColorResource(R.array.hour_list_lesson_separator, state);
            separator.setBackgroundColor(color);

            containerCells.addView(separator);
        }

        private void addLessonCell(LayoutInflater inflater,
                                   ViewGroup containerCells,
                                   TimetableSubject subject,
                                   boolean primary,
                                   boolean active) {

            View cell = inflater.inflate(R.layout.hour_list_lesson, containerCells, false);

            TextView textName = (TextView) cell.findViewById(R.id.text_name);
            TextView textClassroom = (TextView) cell.findViewById(R.id.text_classroom);

            textName.setText(subject.getName());
            textClassroom.setText(subject.getClassroom());

            int state = (active ? 0b10 : 0) + (primary ? 0b01 : 0);
            int nameColor = getColorResource(R.array.hour_list_lesson_name, state);
            int classroomColor = getColorResource(R.array.hour_list_classroom, state);

            textName.setTextColor(nameColor);
            textClassroom.setTextColor(classroomColor);

            containerCells.addView(cell);
        }
    }
}
