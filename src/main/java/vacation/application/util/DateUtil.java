package vacation.application.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DateUtil {

    public static int daysInclusive(LocalDate start, LocalDate end) {
        return (int) ChronoUnit.DAYS.between(start, end) + 1;
    }

    public static List<LocalDate> expand(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1)).toList();
    }
}
