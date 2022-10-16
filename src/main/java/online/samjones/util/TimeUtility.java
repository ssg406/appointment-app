package online.samjones.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class containing date/time formatting tools.
 */
public abstract class TimeUtility {
    public static final DateTimeFormatter TABLE_FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");
    public static final DateTimeFormatter TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    /**
     * Generates timestamp strings for use in application logs
     * @return String value of timestamp appended with "UTC"
     */
    public static String getCurrentTimestampUTC(){
        Instant instant = Instant.now();
        return instant.toString() + "UTC";
    }

    /**
     * Gets local hour of business opening
     * @return local business open hour as int
     */
    public static int getLocalBusinessOpenHour(){
        ZonedDateTime businessOpen = ZonedDateTime.of(LocalDate.now(),
                LocalTime.of(10,0), ZoneId.of("US/Eastern"));

        return businessOpen.withZoneSameInstant(ZoneId.systemDefault()).getHour();
    }

    /**
     * Gets local hour of business closing
     * @return local business closing hour as int
     */
    public static int getLocalBusinessCloseHour(){
        ZonedDateTime businessClose = ZonedDateTime.of(LocalDate.now(),
                LocalTime.of(22,0), ZoneId.of("US/Eastern"));

        return businessClose.withZoneSameInstant(ZoneId.systemDefault()).getHour();
    }
}

