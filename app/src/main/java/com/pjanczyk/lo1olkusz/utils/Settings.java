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

package com.pjanczyk.lo1olkusz.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.pjanczyk.lo1olkusz.R;

import org.joda.time.LocalDate;

import java.util.HashSet;
import java.util.Set;

public class Settings {

    private final Context context;
    private final SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Settings(Context context) {
        this.context = context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Settings edit() {
        editor = preferences.edit();
        return this;
    }

    public void apply() {
        editor.apply();
        editor = null;
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    private boolean getBoolean(@StringRes int keyId, @BoolRes int defaultValueId) {
        return getBoolean(
                context.getResources().getString(keyId),
                context.getResources().getBoolean(defaultValueId));
    }


    //region User settings
    public int getUserNumber() {
        String key = context.getString(R.string.user_number_key);
        String string = preferences.getString(key, null);
        if (string == null) {
            return -1;
        }

        try {
            return Integer.parseInt(string);
        } catch (Exception e) {
            return -1;
        }
    }

    @Nullable
    public String getUserClass() {
        String key = context.getString(R.string.user_class_key);
        return preferences.getString(key, null);
    }

    @NonNull
    public Set<String> getUserGroups() {
        String key = context.getString(R.string.user_groups_key);
        return preferences.getStringSet(key, new HashSet<String>());
    }

    public boolean getLNNotify() {
        return getBoolean(
                R.string.ln_notify_key,
                R.bool.ln_notify_default);
    }

    public boolean getLNNotifySound() {
        return getBoolean(
                R.string.ln_sound_key,
                R.bool.ln_sound_default);
    }

    public boolean getLNNotifyVibration() {
        return getBoolean(
                R.string.ln_vibration_key,
                R.bool.ln_vibration_default);
    }

    public boolean getReplacementsNotify() {
        return getBoolean(
                R.string.replacements_notify_key,
                R.bool.replacements_notify_default);
    }

    public boolean getReplacementsNotifySound() {
        return getBoolean(
                R.string.replacements_sound_key,
                R.bool.replacements_sound_default);
    }

    public boolean getReplacementsVibration() {
        return getBoolean(
                R.string.replacements_vibration_key,
                R.bool.replacements_vibration_default);
    }
    //endregion


    //region Internal synchronization data
    public int getApiTimestamp() {
        return preferences.getInt("apiTimestamp", 0);
    }
    public Settings setApiTimestamp(int value) {
        editor.putInt("apiTimestamp", value);
        return this;
    }

    /* Time of a last <b>successful</b> synchronization or -1 */
    public long getLastSyncTime() {
        return preferences.getLong("lastSuccessSync", -1);
    }
    public Settings setLastSyncTime(long value) {
        editor.putLong("lastSuccessSync", value);
        return this;
    }

    public LocalDate getLastCleanUp() {
        String text = preferences.getString("lastCleanUp", null);
        if (text == null) return null;
        return LocalDate.parse(text);
    }
    public void setLastCleanUp(LocalDate date) {
        editor.putString("lastCleanUp", date.toString());
    }

    @Nullable
    public Integer getRemoteVersion() {
        long value = preferences.getInt("remoteVersion", -1);
        return value == -1 ? null : (int) value;
    }
    public Settings setRemoteVersion(@Nullable Integer value) {
        editor.putInt("remoteVersion", value == null ? -1 : value);
        return this;
    }
    //endregion


    //region Other internal data
    public boolean getFirstRun() {
        return getBoolean("firstRun", true);
    }
    public Settings setFirstRun(boolean value) {
        editor.putBoolean("firstRun", value);
        return this;
    }

    public boolean getSettingsChanged() {
        return getBoolean("settingsChanged", false);
    }
    public Settings setSettingsChanged(boolean value) {
        editor.putBoolean("settingsChanged", value);
        return this;
    }
    //endregion
}
