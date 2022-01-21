package org.runaway.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String getDuration(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis *= 1000L);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis -= TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis -= TimeUnit.MINUTES.toMillis(minutes));
        StringBuilder sb = new StringBuilder(64);
        if (hours > 0L) {
            sb.append(hours);
            sb.append(" \u0447, ");
        }
        if (minutes > 0L) {
            sb.append(minutes);
            sb.append(" \u043c\u0438\u043d, ");
        }
        sb.append(seconds);
        sb.append(" \u0441\u0435\u043a.");
        return sb.toString();
    }
}
