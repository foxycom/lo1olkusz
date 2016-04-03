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

import com.pjanczyk.lo1olkusz.json.TimetableSerializer;
import com.pjanczyk.lo1olkusz.model.Timetable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages timetables saved on the local storage.
 * They are saved as
 * [application files]/timetables/[class name]
 */
public class SavedTimetables {

    private final Context context;

    public SavedTimetables(Context context) {
        this.context = context;
    }

    /**
     * Lists classes of which the timetables are saved on the local storage
     * @return the names of the classes
     */
    public List<String> getAvailableTimetables() {
        ArrayList<String> result = new ArrayList<>();

        String rootPath = context.getFilesDir().toString() + "/timetables";
        File root = new File(rootPath);

        if (root.exists()) {
            for (File file : root.listFiles()) {
                if (file.isFile()) {
                    result.add(file.getName());
                }
            }
        }

        return result;
    }

    /**
     * Loads a timetable of a class from the local storage
     * @param className the name of the class
     * @return the timetable of the class or null if it was not found
     */
    public Timetable load(String className) {
        String path = buildPath(className);
        return FilesManager.load(path, new TimetableSerializer());
    }

    /**
     * Saves a timetable on the local storage, overwriting existing one
     * @param timetable the timetable to be saved
     */
    public void save(Timetable timetable) {
        String path = buildPath(timetable.getClassName());
        FilesManager.save(timetable, path, new TimetableSerializer());
    }

    private String buildPath(String className) {
        return context.getFilesDir().toString()
                + "/timetables/" + className;
    }
}
