package com.jobportal.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private DateUtil() { }

    // 2026-10-20T14:30 becomes "20 Oct 2026"
    public static String format(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(FORMAT);
    }
}