package ru.practicum.constants;

import java.time.format.DateTimeFormatter;

public interface DataTransferConvention {
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

}
