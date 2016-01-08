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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.pjanczyk.lo1olkusz.model.Bells;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.model.TimetableDay;
import com.pjanczyk.lo1olkusz.utils.LocalDateTypeAdapter;
import com.pjanczyk.lo1olkusz.utils.LocalTimeTypeAdapter;
import com.pjanczyk.lo1olkusz.utils.Urls;
import com.pjanczyk.lo1olkusz.utils.network.BadResponseException;
import com.pjanczyk.lo1olkusz.utils.network.HttpStatusCodeException;
import com.pjanczyk.lo1olkusz.utils.network.HttpUtils;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.Collections;

public class Api {

    /**
     * @throws IOException          if an error occurs related to connection and getting response
     * @throws BadResponseException if gets invalid response from server
     *                              (not specified content-length in header,
     *                              or invalid binary format)
     */
    @NonNull
    public static News getNews(String androidId, int version, int timestamp) throws IOException, BadResponseException {
        try {
            String response = HttpUtils.getString(buildUrl(androidId, version, timestamp));

            News news = decodeJson(response);

            //Server should return only version greater than specified in request.
            //Additional check if something went wrong.
            if (news.version == version) {
                news.version = null;
            }

            return news;

        } catch (HttpStatusCodeException | JsonSyntaxException e) {
            throw new BadResponseException(e);
        }
    }

    static News decodeJson(String response) throws JsonSyntaxException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(Bells.TimeSpan.class, new Bells.TimeSpan.Deserializer())
                .registerTypeAdapter(TimetableDay.class, new TimetableDay.Deserializer())
                .create();

        News news = gson.fromJson(response, News.class);

        if (news.replacements == null) {
            news.replacements = Collections.emptyList();
        }
        if (news.timetables == null) {
            news.timetables = Collections.emptyList();
        }
        if (news.luckyNumbers == null) {
            news.luckyNumbers = Collections.emptyList();
        }

        return news;
    }

    static String buildUrl(String androidId, int version, int timestamp) {
        return String.format(Urls.API, timestamp, androidId, version);
    }
}
