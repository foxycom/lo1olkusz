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

package com.pjanczyk.lo1olkusz.storage;

import android.content.Context;
import android.support.annotation.Nullable;

import com.pjanczyk.lo1olkusz.json.LuckyNumberSerializer;
import com.pjanczyk.lo1olkusz.model.LuckyNumber;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages data of lucky numbers, which are saved (encoded in json) on the local storage as
 * [application files]/lucky_numbers/yyyy-MM-dd
 */
public class SavedLuckyNumbers {

    private final Context context;

    public SavedLuckyNumbers(Context context) {
        this.context = context;
    }

    /**
     * Removes replacements older than 3 days
     */
    public void cleanUp() {
        LocalDate limit = LocalDate.now().minusDays(3);
        FilesManager.deleteOlderThan(buildPath(), limit);
    }

    public List<LuckyNumber> loadAll(LocalDate since) {
        List<LuckyNumber> results = new ArrayList<>();

        File root = new File(buildPath());
        if (root.isDirectory()) {
            for (String fileName : root.list()) {
                LocalDate date = null;
                try {
                    date = LocalDate.parse(fileName);
                } catch (Exception ignored) {
                }

                if (date != null && !date.isBefore(since)) {
                    LuckyNumber n = load(date);
                    if (n != null) {
                        results.add(n);
                    }
                }
            }
        }

        return results;
    }

    /**
     * Loads data of lucky number on {@param date} from the local storage
     * @return Data of lucky number or null if it does not exist
     */
    @Nullable
    public LuckyNumber load(LocalDate date) {
        String path = buildPath(date);
        return FilesManager.load(path, new LuckyNumberSerializer());
    }

    /**
     * Saves data of lucky number on the local storage, overwriting existing one
     * @param ln the data to be saved
     */
    public void save(LuckyNumber ln) {
        String path = buildPath(ln.getDate());
        FilesManager.save(ln, path, new LuckyNumberSerializer());
    }

    private String buildPath() {
        return context.getFilesDir().toString() + "/lucky_numbers";
    }

    private String buildPath(LocalDate date) {
        return context.getFilesDir().toString()
                + "/lucky_numbers/" + date.toString();
    }
}
