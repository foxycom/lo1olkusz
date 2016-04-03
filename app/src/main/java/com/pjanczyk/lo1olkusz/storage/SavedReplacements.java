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

import com.pjanczyk.lo1olkusz.json.ReplacementsSerializer;
import com.pjanczyk.lo1olkusz.model.Replacements;

import org.joda.time.LocalDate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages replacements saved on the local storage.
 * They are saved as
 * [application files]/replacements/yyyy-MM-dd/[class name]
 */
public class SavedReplacements {

    private final Context context;

    public SavedReplacements(Context context) {
        this.context = context;
    }

    /**
     * Loads replacements of {@param className} at {@param date} from the local storage
     * @return Replacements or null if them does not exist
     */
    @Nullable
    public Replacements load(String className, LocalDate date) {
        String path = buildPath(className, date);
        return FilesManager.load(path, new ReplacementsSerializer());
    }

    /**
     * Saves replacements on the local storage, overwriting existing ones.
     * @param replacements replacements to be saved
     */
    public void save(Replacements replacements) {
        String path = buildPath(replacements.getClassName(), replacements.getDate());
        FilesManager.save(replacements, path, new ReplacementsSerializer());
    }

    /**
     * Removes replacements older than 3 days
     */
    public void cleanUp() {
        LocalDate limit = LocalDate.now().minusDays(3);
        FilesManager.deleteOlderThan(buildPath(), limit);
    }

    public List<Replacements> loadAll(LocalDate since) {
        List<Replacements> results = new ArrayList<>();

        File root = new File(buildPath());
        if (root.exists()) {
            for (File dateDir : root.listFiles()) {
                LocalDate date = null;
                try {
                    date = LocalDate.parse(dateDir.getName());
                } catch (Exception ignored) {
                }

                if (date != null && !date.isBefore(since) && dateDir.isDirectory()) {
                    for (String className : dateDir.list()) {
                        Replacements r = load(className, date);
                        if (r != null) {
                            results.add(r);
                        }
                    }
                }
            }
        }

        return results;
    }

    private String buildPath() {
        return context.getFilesDir().toString() + "/replacements";
    }

    private String buildPath(String className, LocalDate date) {
        return context.getFilesDir().toString()
                + "/replacements/" + date
                + "/" + className;
    }
}
