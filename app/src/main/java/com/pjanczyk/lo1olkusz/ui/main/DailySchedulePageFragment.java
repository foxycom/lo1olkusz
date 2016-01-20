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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;
import com.pjanczyk.lo1olkusz.storage.SavedLuckyNumbers;
import com.pjanczyk.lo1olkusz.model.Replacements;
import com.pjanczyk.lo1olkusz.storage.SavedReplacements;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.utils.TimeFormatter;

import org.joda.time.LocalDate;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class DailySchedulePageFragment extends Fragment {

    private final static String ARG_DATE = "date";
    private final static String ARG_BELLS = "bells";
    private final static String ARG_TIMETABLE_DAY = "timetableDay";
    private final static String ARG_CLASS_NAME = "className";
    private final static String ARG_GROUPS = "groups";

    private LocalDate date;
    private int luckyNumber = -1;
    private Bells bells;
    private TimetableDay timetableDay;
    private Replacements replacements;
    private String className;
    private Set<String> groups;

    public static DailySchedulePageFragment newInstance(LocalDate date,
                                                        String className,
                                                        Bells bells,
                                                        TimetableDay timetableDay,
                                                        Set<String> groups) {

        DailySchedulePageFragment fragment = new DailySchedulePageFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.toDate().getTime());
        args.putParcelable(ARG_BELLS, bells);
        args.putParcelable(ARG_TIMETABLE_DAY, timetableDay);
        args.putString(ARG_CLASS_NAME, className);
        if (groups == null) {
            args.putStringArray(ARG_GROUPS, null);
        } else {
            args.putStringArray(ARG_GROUPS, groups.toArray(groups.toArray(new String[groups.size()])));
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = new LocalDate(getArguments().getLong(ARG_DATE));
            bells = getArguments().getParcelable(ARG_BELLS);
            timetableDay = getArguments().getParcelable(ARG_TIMETABLE_DAY);
            className = getArguments().getString(ARG_CLASS_NAME);

            String[] groupsArray = getArguments().getStringArray(ARG_GROUPS);
            if (groupsArray != null) {
                groups = new TreeSet<>(Arrays.asList(groupsArray));
            }
        }

        SavedLuckyNumbers savedLNs = new SavedLuckyNumbers(getActivity().getApplicationContext());
        LuckyNumber ln = savedLNs.load(date);
        if (ln != null) {
            luckyNumber = ln.getValue();
        }

        SavedReplacements savedRepls = new SavedReplacements(getActivity().getApplicationContext());
        replacements = savedRepls.load(className, date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.daily_schedule_page, container, false);

        TextView textDate = (TextView) view.findViewById(R.id.text_date);
        TextView textWeekDay = (TextView) view.findViewById(R.id.text_day_of_week);
        TextView textLuckyNumber = (TextView) view.findViewById(R.id.text_lucky_number_value);

        textDate.setText(TimeFormatter.dayAndMonth(date));
        textWeekDay.setText(TimeFormatter.dayOfWeek(date));

        if (luckyNumber == -1) {
            textLuckyNumber.setVisibility(View.GONE);
        }
        else {
            textLuckyNumber.setVisibility(View.VISIBLE);
            textLuckyNumber.setText(Integer.toString(luckyNumber));
        }

        HourList hourList = (HourList) view.findViewById(R.id.hour_list);

        if (timetableDay != null) {
            hourList.setContent(timetableDay, bells, groups, replacements);
        }

        return view;
    }
}
