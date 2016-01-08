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

package com.pjanczyk.lo1olkusz.ui.firstrun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pjanczyk.lo1olkusz.R;

/**
 * The second screen of {@link FirstRunActivity}
 * Allows user to choose his class, groups and number.
 */
public class FirstRunUserDetailsFragment extends Fragment {

    private Button buttonSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_run_user_details, container, false);

        buttonSave = (Button) view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstRunActivity activity = (FirstRunActivity) getActivity();
                activity.onButtonSaveClick();
            }
        });

        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.frame_content, new FirstRunSettingsFragment())
                    .commit();
        }

        return view;
    }

	/** Called by {@link FirstRunSettingsFragment} */
    void setSettingsSet(boolean settingsSet) {
        buttonSave.setEnabled(settingsSet);
    }

}
