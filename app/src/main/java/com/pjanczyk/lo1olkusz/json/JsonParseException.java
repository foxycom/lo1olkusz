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

package com.pjanczyk.lo1olkusz.json;

public class JsonParseException extends Exception {

    public JsonParseException() {
    }

    public JsonParseException(String detailMessage) {
        super(detailMessage);
    }

    public JsonParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public JsonParseException(Throwable throwable) {
        super(throwable);
    }
}
