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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.model.TimetableSubject;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

//TODO: add a progress bar that shows elapsed time
public class HourList extends LinearLayout {

    private TimetableDay timetableDay;
    private Bells bells;
    private Set<String> groups;
    private Replacements replacements;

    public HourList(Context context) {
        super(context);
        init();
    }

    public HourList(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HourList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        final int sizeInDp = 5;
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (sizeInDp * scale + 0.5f);

        setPadding(0, dpAsPixels, 0, dpAsPixels);
        setOrientation(VERTICAL);
    }

    public void setContent(TimetableDay timetableDay,
                         Bells bells,
                         Set<String> groups,
                         Replacements replacements) {
        this.timetableDay = timetableDay;
        this.bells = bells;
        this.groups = groups;
        this.replacements = replacements;

        initializeViews();
    }

    public void initializeViews() {

        removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (timetableDay != null) {

            int lastHour = -1;

            for (Map.Entry<Integer, TimetableSubject[]> entry : timetableDay.getSubjects().entrySet()) {
                int hour = entry.getKey();
                List<TimetableSubject> subjects = Arrays.asList(entry.getValue());

                List<TimetableSubject> primary = new ArrayList<>();
                List<TimetableSubject> secondary = new ArrayList<>();
                separateSubjects(subjects, primary, secondary);

                if (!primary.isEmpty() || !secondary.isEmpty()) {

                    if (lastHour != -1) {
                        //add empty rows
                        while (hour - lastHour > 1) {
                            lastHour++;
                            addRow(inflater, lastHour,
                                    Collections.<TimetableSubject>emptyList(),
                                    Collections.<TimetableSubject>emptyList(),
                                    null);
                        }
                    }

                    String replacement = null;
                    if (replacements != null) {
                        replacement = replacements.atHour(hour);
                    }

                    addRow(inflater, hour, primary, secondary, replacement);

                    lastHour = hour;
                }
            }
        }
    }

    private void separateSubjects(List<TimetableSubject> subjects,
                                  List<TimetableSubject> primary,
                                  List<TimetableSubject> secondary) {

        if (groups == null || groups.size() == 0) {
            primary.addAll(subjects);
        } else {

            List<TimetableSubject> temp = new ArrayList<>();

            for (TimetableSubject subject : subjects) {
                if (subject.getGroup() == null) {
                    temp.add(subject);
                } else if (groups.contains(subject.getGroup())) {
                    primary.add(subject);
                } else {
                    secondary.add(subject);
                }
            }

            if (primary.size() == 0) {
                primary.addAll(temp);
            }
            else {
                secondary.addAll(temp);
            }
        }
    }
    private void addRow(LayoutInflater inflater,
                        int hour,
                        Collection<TimetableSubject> primary,
                        Collection<TimetableSubject> secondary,
                        String replacement) {

        if (this.getChildCount() > 0) {
            View separator = inflater.inflate(R.layout.hours_list_row_separator, this, false);

            LocalTime breakBegin = null;
            LocalTime breakEnd = null;

            if (bells != null) {
                breakBegin = bells.getHourEnd(hour - 1);
                breakEnd = bells.getHourBegin(hour);
            }

            if (breakBegin != null && breakEnd != null) {
                int minutes = (breakEnd.getMillisOfDay() - breakBegin.getMillisOfDay()) / 60000;

                TextView minutesText = (TextView) separator.findViewById(R.id.minutes_text);
                minutesText.setText(Integer.toString(minutes));
            }

            this.addView(separator);
        }

        int resId = primary.size() == 0 ? R.layout.hours_list_row_secondary : R.layout.hours_list_row;
        View row = inflater.inflate(resId, this, false);

        ViewGroup containerCells = (ViewGroup) row.findViewById(R.id.container_cells);
        TextView textHour = (TextView) row.findViewById(R.id.text_hour);
        TextView textBegin = (TextView) row.findViewById(R.id.text_begin);
        TextView textEnd = (TextView) row.findViewById(R.id.text_end);
        TextView textReplacement = (TextView) row.findViewById(R.id.text_replacement);

        textHour.setText(hour + ".");

        LocalTime beginTime = null;
        LocalTime endTime = null;
        if (bells != null) {
            beginTime = bells.getHourBegin(hour);
            endTime = bells.getHourEnd(hour);
        }
        textBegin.setText(beginTime == null ? null : beginTime.toString("HH:mm"));
        textEnd.setText(endTime == null ? null : endTime.toString("HH:mm"));

        boolean firstCell = true;
        for (TimetableSubject subject : primary) {
            if (!firstCell) {
                inflater.inflate(R.layout.hours_list_cell_separator, containerCells);
            }
            addCell(inflater, containerCells, subject, true);
            firstCell = false;
        }
        for (TimetableSubject subject : secondary) {
            if (!firstCell) {
                inflater.inflate(R.layout.hours_list_cell_separator, containerCells);
            }
            addCell(inflater, containerCells, subject, false);
            firstCell = false;
        }

        if (replacement != null) {
            textReplacement.setVisibility(View.VISIBLE);
            textReplacement.setText(replacement);
        }

        this.addView(row);
    }

    private void addCell(LayoutInflater inflater,
                         ViewGroup containerCells,
                         TimetableSubject subject,
                         boolean primary) {

        int resourceId = primary ? R.layout.hours_list_cell : R.layout.hours_list_cell_secondary;

        View cell = inflater.inflate(resourceId, containerCells, false);

        TextView textName = (TextView) cell.findViewById(R.id.text_name);
        TextView textClassroom = (TextView) cell.findViewById(R.id.text_classroom);

        textName.setText(subject.getName());
        textClassroom.setText(subject.getClassroom());

        containerCells.addView(cell);
    }
}
