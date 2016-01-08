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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.pjanczyk.lo1olkusz.ui.main.MainActivity;
import com.pjanczyk.lo1olkusz.utils.Settings;

public class FirstRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, new FirstRunWelcomeFragment())
                    .commit();
        }
    }

    /** Called by {@link FirstRunWelcomeFragment} */
    public void onButtonNextClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //noinspection ResourceType
        fragmentManager.beginTransaction()
                .add(android.R.id.content, new FirstRunUserDetailsFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    /** Called by {@link FirstRunUserDetailsFragment} */
    public void onButtonSaveClick() {
        Settings settings = new Settings(this);
        settings.edit()
                .setFirstRun(false)
                .apply();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

}