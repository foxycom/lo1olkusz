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

import com.pjanczyk.lo1olkusz.json.BellsSerializer;
import com.pjanczyk.lo1olkusz.model.Bells;

public class SavedBells {

    private final Context context;

    public SavedBells(Context context) {
        this.context = context;
    }

    /**
     * Loads bells from the local storage
     */
    @Nullable
    public Bells load() {
        String path = buildPath();
        return FilesManager.load(path, new BellsSerializer());
    }

    /**
     * Saves bells on the local storage, overwriting existing one
     */
    public void save(Bells bells) {
        String path = buildPath();
        FilesManager.save(bells, path, new BellsSerializer());
    }

    private String buildPath() {
        return context.getFilesDir().toString() + "/bells/default";
    }
}
