package backend.datn.helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidate {
    public static boolean isValidDate(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        String dateRegex = "^(0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[0-2])\\/\\d{4}$";
        return date.matches(dateRegex);
    }

    public static boolean isValidTime(String time) {
        if (time == null || time.isEmpty()) {
            return false;
        }
        String timeRegex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
        return time.matches(timeRegex);
    }


    public static boolean isDateInPast(String date) {
        if (!isValidDate(date)) {
            return false;
        }
        try {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return parsedDate.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isDateInFuture(String date) {
        if (!isValidDate(date)) {
            return false;
        }
        try {
            LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return parsedDate.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
