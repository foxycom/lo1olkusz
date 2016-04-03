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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pjanczyk.lo1olkusz.json.Serializer;
import com.pjanczyk.lo1olkusz.model.Emptyable;
import com.pjanczyk.lo1olkusz.utils.FileUtils;
import com.pjanczyk.lo1olkusz.json.JsonParseException;

import org.joda.time.LocalDate;

import java.io.File;
import java.io.IOException;

final class FilesManager {

    /**
     * Loads json from a file at {@param path}
     * and converts it to object.
     * If file is corrupted deletes it.
     * If file does not exist, returns null.
     */
    @Nullable
    public static <T extends Emptyable> T load(@NonNull String path, Serializer<T> serializer) {
        try {
            String content = FileUtils.readFromFile(path);
            if (content == null) return null; //if file doesn't exist

            T result = serializer.deserialize(content);
            if (result == null || result.isEmpty()) {
                delete(path); //delete the corrupted file
                return null;
            }

            return result;
        }
        catch (JsonParseException | IOException e) {
            throw new RuntimeException(e); //this should never happen
        }
    }

    /**
     * Converts {@param object} to json and saves it at {@param path} on the local storage.
     * If object is empty, does not save it but deletes an existing file.
     */
    public static <T extends Emptyable> void save(@NonNull T object,
                                                  @NonNull String path,
                                                  @NonNull Serializer<T> serializer) {
        if (object.isEmpty()) {
            delete(path);
        }
        else {
            String content = serializer.serialize(object);
            try {
                FileUtils.createDirectoriesForFile(path);
                FileUtils.writeToFile(path, content);
            } catch (IOException e) {
                throw new RuntimeException(e); //this should have never happen
            }
        }
    }

    public static void deleteOlderThan(String dirPath, LocalDate olderThan) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                LocalDate date = null;
                try {
                    date = LocalDate.parse(file.getName());
                } catch (Exception ignored) {
                }

                if (date == null || date.isBefore(olderThan)) {
                    delete(file.getPath());
                }
            }
        }
    }

    private static void delete(@NonNull String path) {
        try {
            FileUtils.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e); //this should have never happen
        }
    }
}
