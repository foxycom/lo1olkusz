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
import com.pjanczyk.lo1olkusz.utils.TimeFormatter;

public class TimetableFragment extends FragmentBase {

    private TextView timetableLoadingErrorText;
    private ViewPager viewPager;
    private Adapter pagerAdapter;
    private TabLayout tabs;

    private Bells bells;
    private Timetable timetable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timetable, container, false);

        tabs = (TabLayout) view.findViewById(R.id.tabs);
        timetableLoadingErrorText = (TextView) view.findViewById(R.id.text_timetable_loading_error);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        //load content
        updateContent();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        //setup ViewPager & tabs
        pagerAdapter = new Adapter(getChildFragmentManager());
        pagerAdapter.shortTabTitle = (displayMetrics.widthPixels / displayMetrics.density < 550);
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        //page margin
        int dpPageWidth = 416; //400dp + 2x 8dp margin
        int pxPageWidth = (int) (dpPageWidth * displayMetrics.density + 0.5f);
        if (displayMetrics.widthPixels > pxPageWidth) {
            //set negative margin to reduce space between pages
            int pxMargin = (pxPageWidth - displayMetrics.widthPixels) / 2;
            viewPager.setPageMargin(pxMargin);
        }

        return view;
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

        if (timetable != null) {
            timetableLoadingErrorText.setVisibility(View.GONE);
            tabs.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);

            if (pagerAdapter != null) {
                pagerAdapter.notifyDataSetChanged();
            }
        } else {
            //show error message
            timetableLoadingErrorText.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.GONE);
            viewPager.setVisibility(View.GONE);
        }
    }

    private class Adapter extends FragmentStatePagerAdapter {

        boolean shortTabTitle;

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return timetable == null ? 0 : 5;
        }

        @Override
        public Fragment getItem(int position) {
            if (timetable != null) {
                return TimetablePageFragment.newInstance(
                        bells,
                        timetable.getDay(position),
                        activity.getCurrentGroups());
            } else {
                return new Fragment(); //empty placeholder
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (shortTabTitle) {
                return TimeFormatter.dayOfWeekShort(position + 1);
            } else {
                return TimeFormatter.dayOfWeek(position + 1);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}
