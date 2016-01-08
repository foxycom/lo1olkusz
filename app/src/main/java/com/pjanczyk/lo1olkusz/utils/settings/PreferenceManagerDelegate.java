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

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Helper class that allows create PreferenceScreen from resource
 * Uses reflection to create PreferenceManager, which doesn't have public constructors.
 * It should work on all api levels.
 */
class PreferenceManagerDelegate {

    private PreferenceManager mgr;

    public PreferenceManagerDelegate(Activity activity, int firstRequestCode) {
        try {
            Constructor<PreferenceManager> constructor = PreferenceManager.class.getDeclaredConstructor(Activity.class, int.class);
            constructor.setAccessible(true);
            mgr = constructor.newInstance(activity, firstRequestCode);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot instate PreferenceManager", e);
        }
    }

    /**
     * Inflates a preference hierarchy from XML. If a preference hierarchy is
     * given, the new preference hierarchies will be merged in.
     *
     * @param context The context of the resource.
     * @param resId The resource ID of the XML to inflate.
     * @param rootPreferences Optional existing hierarchy to merge the new
     *            hierarchies into.
     * @return The root hierarchy (if one was not provided, the new hierarchy's
     *         root).
     */
    public PreferenceScreen inflateResources(Context context, int resId,
                                             PreferenceScreen rootPreferences) {
        try {
            Method method = PreferenceManager.class.getDeclaredMethod(
                    "inflateFromResource", Context.class, int.class, PreferenceScreen.class);
            method.setAccessible(true);

            return (PreferenceScreen) method.invoke(mgr, context, resId, rootPreferences);
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot invoke PreferenceManager#inflateResources", e);
        }
    }
}
