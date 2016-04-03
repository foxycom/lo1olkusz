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

package com.pjanczyk.lo1olkusz.synchronization;

import android.support.annotation.NonNull;

import com.pjanczyk.lo1olkusz.json.Serializer;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.json.JsonParseException;
import com.pjanczyk.lo1olkusz.utils.Urls;
import com.pjanczyk.lo1olkusz.utils.network.BadResponseException;
import com.pjanczyk.lo1olkusz.utils.network.HttpHelper;
import com.pjanczyk.lo1olkusz.utils.network.HttpStatusCodeException;

import java.io.IOException;

class ApiImpl {

    private HttpHelper http;
    private Serializer<News> serializer;

    public ApiImpl(HttpHelper httpHelper, Serializer<News> serializer) {
        this.http = httpHelper;
        this.serializer = serializer;
    }

    /**
     * @throws IOException          if an error occurs related to connection and getting response
     * @throws BadResponseException if gets invalid response from server
     *                              (not specified content-length in header,
     *                              or invalid binary format)
     */
    @NonNull
    public News getNews(String androidId, int version, int timestamp)
            throws IOException, BadResponseException {

        String url = buildUrl(androidId, version, timestamp);

        String response;
        try {
            response = http.getString(url);
            return serializer.deserialize(response);
        } catch (HttpStatusCodeException | JsonParseException e) {
            throw new BadResponseException(e);
        }
    }

    private String buildUrl(String androidId, int version, int timestamp) {
        return String.format(Urls.API, timestamp, androidId, version);
    }
}
