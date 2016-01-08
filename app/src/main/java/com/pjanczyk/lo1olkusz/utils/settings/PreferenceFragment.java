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

package com.pjanczyk.lo1olkusz.utils.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PreferenceFragment extends Fragment {

    private ListView listView;
    private PreferenceScreen preferenceScreen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = new ListView(getContext());
        int sizeInDp = 10;
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (sizeInDp*scale + 0.5f);
        listView.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        return listView;
    }

    public void addPreferencesFromResource(@XmlRes int preferencesResId) {
        PreferenceManagerDelegate mgr = new PreferenceManagerDelegate(getActivity(), 0);
        preferenceScreen = mgr.inflateResources(getContext(), preferencesResId, null);
        preferenceScreen.bind(listView);
    }

    public Preference findPreference(CharSequence key) {
        return preferenceScreen.findPreference(key);
    }
}
