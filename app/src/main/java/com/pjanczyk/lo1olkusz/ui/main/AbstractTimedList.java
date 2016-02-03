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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.util.Map;
import java.util.TreeMap;

/**
 * A view that shows events in a vertically list with highlighted the current one.
 * Each event has specified times of its beginning and ending.
 */
public abstract class AbstractTimedList extends LinearLayout {

    private final Handler handler = new Handler();
    private final TreeMap<LocalTime, Integer> timeToViewIndexMap = new TreeMap<>();
    private int activeView = -1;

    public AbstractTimedList(Context context) {
        this(context, null);
    }

    public AbstractTimedList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractTimedList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    protected final void notifyDataSetChanged() {
        reloadAllViews();
    }

    protected abstract View createItemView(int position,
                                           ViewGroup parent,
                                           boolean active);

    protected abstract LocalTime getItemBeginTime(int position);

    protected abstract LocalTime getItemEndTime(int position);

    protected abstract int getItemCount();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.removeCallbacksAndMessages(null);
        updateActive();
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private void updateActive() {
        if (activeView != -1) {
            reloadView(activeView, false);
            activeView = -1;
        }

        LocalTime now = LocalTime.now();
        Map.Entry<LocalTime, Integer> entry = timeToViewIndexMap.floorEntry(now);
        if (entry != null) {
            int vh = entry.getValue();
            if (vh != -1) {
                reloadView(vh, true);
                activeView = vh;
            }
        }

        LocalTime next = timeToViewIndexMap.ceilingKey(now);
        if (next != null) {
            int millis = new Period(now, next).getMillis();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateActive();
                }
            }, millis);
        }
    }

    private void reloadAllViews() {
        timeToViewIndexMap.clear();
        this.removeAllViews();

        int count = getItemCount();

        for (int i = 0; i < count; i++) {
            boolean active = (activeView == i);

            View view = createItemView(i, this, active);
            addView(view);

            LocalTime begin = getItemBeginTime(i);
            LocalTime end = getItemEndTime(i);

            if (begin != null && end != null) {
                timeToViewIndexMap.put(begin, i);
                if (!timeToViewIndexMap.containsKey(end)) {
                    timeToViewIndexMap.put(end, -1);
                }
            }
        }

        updateActive();
    }

    private void reloadView(int pos, boolean active) {
        removeViewAt(pos);
        View view = createItemView(pos, this, active);
        addView(view, pos);
    }
}
