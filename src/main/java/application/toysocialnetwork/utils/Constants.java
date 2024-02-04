package application.toysocialnetwork.utils;

import java.time.format.DateTimeFormatter;

/**
 * Class for defining application-wide constants
 */
public class Constants {
    public final static short MINIMUM_COMMUNITY_MEMBERS = 2;
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
}
