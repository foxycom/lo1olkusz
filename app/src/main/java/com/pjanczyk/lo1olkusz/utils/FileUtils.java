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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {

    /**
     * Creates the directory at {@param path}, creating missing parent
     * directories if necessary.
     * @throws IOException on failure
     */
    public static void createDirectory(String path) throws IOException {
        File file = new File(path);
        if (!file.mkdirs() && !file.isDirectory()) {
            throw new IOException("Cannot create directory " + path);
        }
    }

    /**
     * Creates a parent directories for a file at {@param path}.
     * @throws IOException on failure
     */
    public static void createDirectoriesForFile(String path) throws IOException {
        File file = new File(path);
        createDirectory(file.getParent());
    }

    public static void writeToFile(String fileName, String data) throws IOException {
        FileOutputStream stream = new FileOutputStream(fileName);
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        writer.write(data);
        writer.close();
    }

    /**
     * Reads the content of a file
     * @return content of the file or null if it doesn't exist
     * @throws IOException on failure
     */
    public static String readFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream stream;
        try {
            stream = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1); //remove appended "\n" from the end
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Deletes a file. If the file a directory, recursively deletes all its contents.
     * @param fileName The name of the file.
     * @throws IOException if cannot delete file
     */
    public static void delete(String fileName) throws IOException {
        delete(new File(fileName));
    }

    /**
     * Deletes a file. If the file a directory, recursively deletes all its contents.
     * @param file The file to be deleted.
     * @throws IOException if cannot delete file
     */
    public static void delete(File file) throws IOException {
        if (!file.exists()) return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                delete(f);
            }
        }

        if (!file.delete()) {
            throw new IOException("Cannot delete file " + file.getPath());
        }
    }
}
