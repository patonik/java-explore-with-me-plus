package ru.practicum.util;

import ru.practicum.DataTransferConvention;

import java.time.LocalDateTime;
import java.util.List;

public interface Statistical {
    LocalDateTime getCreatedOn();

    Long getId();

    static Params getParams(List<Statistical> events) {
        String end = LocalDateTime.now().format(DataTransferConvention.DATE_TIME_FORMATTER);
        String start = events.stream().min(new LocalDateTimeComparator())
            .orElseThrow(() -> new RuntimeException("start date cannot be null")).getCreatedOn()
            .format(DataTransferConvention.DATE_TIME_FORMATTER);
        List<String> uriList = events.stream().map(x -> "/events/" + x.getId()).toList();
        return new Params(start, end, uriList);
    }
}
