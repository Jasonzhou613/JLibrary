/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.custom;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * 自定义的HeaderParser,跟默认的HttpHeaderParser比，可以强制缓存，忽略服务器的设置
 */
public class CHttpHeaderParser extends HttpHeaderParser {

    /**
     * Extracts a {@link com.android.volley.Cache.Entry} from a {@link com.android.volley.NetworkResponse}.
     *
     * @param response  The network response to parse headers from
     * @param cacheTime 缓存时间，如果设置了这个值，不管服务器返回是否可以缓存，都会缓存,单位为：毫秒
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseCacheHeaders(NetworkResponse response, long cacheTime) {

        Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
        if (entry == null) {
            return null;
        }
        long now = System.currentTimeMillis();
        long softExpire = now + cacheTime;
        entry.softTtl = softExpire;
        entry.ttl = entry.softTtl;

        return entry;
    }
}
