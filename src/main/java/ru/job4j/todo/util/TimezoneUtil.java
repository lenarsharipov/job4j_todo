package ru.job4j.todo.util;

import ru.job4j.todo.model.Task;
import ru.job4j.todo.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Utility Timezone class.
 */
public class TimezoneUtil {
    /**
     * Default Timezone used in app.
     */
    public static final String DEFAULT_TIMEZONE_ID = "UTC";

    /**
     * TIMEZONES constant list persists all supported Timezones.
     */
    public static final List<TimeZone> TIMEZONES;

    private TimezoneUtil() {
    }

    static {
        TIMEZONES = new ArrayList<>();
        for (String timeId : TimeZone.getAvailableIDs()) {
            TIMEZONES.add(TimeZone.getTimeZone(timeId));
        }
    }

    /**
     * Convert displayed time upon user's timezone setting.
     * @param task task
     * @param request HttpServletRequest
     */
    public static void convertTaskTime(Task task, HttpServletRequest request) {
        var user = (User) request.getAttribute(Attribute.USER);
        var zoneId = user.getTimezone();
        task.setCreated(
                ZonedDateTime.of(task.getCreated(), ZoneId.of(DEFAULT_TIMEZONE_ID))
                        .withZoneSameInstant(ZoneId.of(zoneId)).toLocalDateTime()
        );
    }

}