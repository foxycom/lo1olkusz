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


import com.pjanczyk.lo1olkusz.json.NewsSerializer;
import com.pjanczyk.lo1olkusz.json.Serializer;
import com.pjanczyk.lo1olkusz.model.News;
import com.pjanczyk.lo1olkusz.utils.network.HttpHelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiTest {

    @Test
    public void testGetNews() throws Exception {
        News expected = new News(123456, null, null, null, null, null);

        HttpHelper httpHelper = mock(HttpHelper.class);

        when(httpHelper.getString("http://lo1olkusz.tk/api/news/2222?aid=AID&v=1111"))
                .thenReturn("RESPONSE");

        Serializer<News> serializer = mock(NewsSerializer.class);
        when(serializer.deserialize("RESPONSE")).thenReturn(expected);

        ApiImpl api = new ApiImpl(httpHelper, serializer);
        News actual = api.getNews("AID", 1111, 2222);

        assertEquals(expected, actual);

    }

}
