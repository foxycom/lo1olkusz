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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pjanczyk.lo1olkusz.R;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.storage.SavedBells;
import com.pjanczyk.lo1olkusz.storage.SavedTimetables;
import com.pjanczyk.lo1olkusz.model.Timetable;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.utils.TimeFormatter;

import org.joda.time.LocalDate;

public class DailyScheduleFragment extends FragmentBase {

    private static final String STATE_PAGER_POS = "pagerPos";
    private static final String ARG_DATE = "date";

    private TextView timetableLoadingErrorText;
    private ViewPager viewPager;
    private TabLayout tabs;
    private PagerAdapter pagerAdapter;

    private Bells bells = null;
    private Timetable timetable = null;

    private int pagerGoTo = -1;

    public static DailyScheduleFragment newInstance(LocalDate date) {
        DailyScheduleFragment fragment = new DailyScheduleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.daily_schedule, container, false);

        timetableLoadingErrorText = (TextView) view.findViewById(R.id.text_timetable_loading_error);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        pagerAdapter = new PagerAdapter(getChildFragmentManager());

        if (viewPager != null) { //layout with ViewPager & tabs
            viewPager.setAdapter(pagerAdapter);

            if (savedInstanceState != null) {
                viewPager.setCurrentItem(savedInstanceState.getInt(STATE_PAGER_POS), false);
            }
            else {
                LocalDate date = (LocalDate) getArguments().getSerializable(ARG_DATE);
                if (date != null && date.equals(pagerAdapter.dates[1])) {
                    pagerGoTo = 1;
                }
            }

            //page margin
            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            int dpPageWidth = 416; //400dp + 2x 8dp margin
            int pxPageWidth = (int) (dpPageWidth * displayMetrics.density + 0.5f);
            if (displayMetrics.widthPixels > pxPageWidth) {
                //set negative margin to reduce space between pages
                int pxMargin = (pxPageWidth - displayMetrics.widthPixels) / 2;
                viewPager.setPageMargin(pxMargin);
            }

            tabs = (TabLayout) view.findViewById(R.id.tabs);

            tabs.setupWithViewPager(viewPager);
        }

        updateContent();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewPager != null) {
            outState.putInt(STATE_PAGER_POS, viewPager.getCurrentItem());
        }
    }

    @Override
    public void onClassOrGroupsChanged() {
        if (getView() != null) { //only if onCreateView has already been called
            updateContent();
        }
    }

    @Override
    public void onSyncCompleted() {
        if (getView() != null) {
            updateContent();
        }
    }

    private void updateContent() {
        bells = new SavedBells(activity).load();

        String className = activity.getCurrentClass();
        if (className != null) {
            timetable = new SavedTimetables(activity).load(className);
        }
        else {
            timetable = null;
        }

        if (timetable == null) {
            timetableLoadingErrorText.setVisibility(View.VISIBLE);
        } else {
            timetableLoadingErrorText.setVisibility(View.GONE);

            if (tabs != null) { //ViewPager & tabs layout
                int currentItem = viewPager.getCurrentItem();
                pagerAdapter.notifyDataSetChanged();
                tabs.setTabsFromPagerAdapter(pagerAdapter);

                if (pagerGoTo != -1) {
                    viewPager.setCurrentItem(pagerGoTo, false);
                    pagerGoTo = -1;
                } else {
                    viewPager.setCurrentItem(currentItem, false);
                }
            } else { //two-pane layout
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_page_1, pagerAdapter.getItem(0))
                        .replace(R.id.frame_page_2, pagerAdapter.getItem(1))
                        .commit();
            }
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        private final LocalDate[] dates = new LocalDate[2];

        public PagerAdapter(FragmentManager fm) {
            super(fm);

            dates[0] = LocalDate.now();
            int weekday = dates[0].getDayOfWeek();
            if (weekday > 5) {
                dates[0] = dates[0].minusDays(weekday - 5);
            }

            for (int i = 1; i < 2; i++) {
                dates[i] = dates[i - 1].plusDays(1);
                weekday = dates[i].getDayOfWeek();
                if (weekday > 5) {
                    dates[i] = dates[i].plusDays(8 - weekday);
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            LocalDate date = dates[position];
            TimetableDay schedule = timetable.getDay(date.getDayOfWeek() - 1);

            return DailySchedulePageFragment.newInstance(
                    date,
                    activity.getCurrentClass(),
                    bells,
                    schedule,
                    activity.getCurrentGroups());
        }

        @Override
        public int getCount() {
            return timetable != null ? 2 : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TimeFormatter.relativeDate(dates[position]);
        }

        @Override
        public int getItemPosition(Object object) {
            //recreate all fragments when calling Adapter#notifyDataSetChanged
            return POSITION_NONE;
        }
    }

}
