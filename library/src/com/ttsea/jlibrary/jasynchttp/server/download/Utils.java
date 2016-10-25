package com.ttsea.jlibrary.jasynchttp.server.download;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * //To do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2016/5/5 11:14 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2016/5/5 11:14
 */
class Utils {

    public static Map<String, String> sortByComparator(Map<String, String> unsortMap) {
        List list = new LinkedList(unsortMap.entrySet());
        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                if (o1 == null || o2 == null
                        || ((Map.Entry) (o1)).getValue() == null
                        || ((Map.Entry) (o2)).getValue() == null) {
                    return 0;
                }
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /** 判断str是否为空 */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() < 1) {
            return true;
        }
        return false;
    }
}
