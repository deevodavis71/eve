package com.sjd.eve.cqrs.utils;

import java.util.Arrays;
import java.util.List;

import com.sjd.eve.cqrs.core.Event;

/**
 * User: stevedavis
 * Date: 20/03/2018
 * Time: 18:58
 * Description:
 */
public class EventUtils {

    public static List<Event> events(Event... events) {
        return Arrays.asList(events);
    }

}