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

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.TimetableDay;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class TimetablePageFragment extends Fragment {

    private final static String ARG_BELLS = "bells";
    private final static String ARG_TIMETABLE_DAY = "timetableDay";
    private final static String ARG_GROUPS = "groups";

    private Bells bells;
    private TimetableDay timetableDay;
    private Set<String> groups;

    public static TimetablePageFragment newInstance(Bells bells,
                                                        TimetableDay timetableDay,
                                                        Set<String> groups) {

        TimetablePageFragment fragment = new TimetablePageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BELLS, bells);
        args.putParcelable(ARG_TIMETABLE_DAY, timetableDay);
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
            bells = getArguments().getParcelable(ARG_BELLS);
            timetableDay = getArguments().getParcelable(ARG_TIMETABLE_DAY);

            String[] groupsArray = getArguments().getStringArray(ARG_GROUPS);
            if (groupsArray != null) {
                groups = new TreeSet<>(Arrays.asList(groupsArray));
            } else {
                groups = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timetable_page, container, false);

        HourList hourList = (HourList) view.findViewById(R.id.hour_list);
        if (timetableDay != null) {
            hourList.setContent(timetableDay, bells, groups, null);
        }

        return view;
    }
}
